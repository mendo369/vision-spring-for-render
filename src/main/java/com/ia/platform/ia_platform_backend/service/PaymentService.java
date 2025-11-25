// src/main/java/com/ia/platform/ia_platform_backend/service/PaymentService.java

package com.ia.platform.ia_platform_backend.service;

import com.ia.platform.ia_platform_backend.entity.*;
import com.ia.platform.ia_platform_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final UserRepository userRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final UserPurchaseWithBalanceRepository userPurchaseWithBalanceRepository;
    private final ToolRepository toolRepository;

    @Value("${wompi.public.key}") // LLAVE PÚBLICA
    private String wompiPublicKey;

    @Value("${wompi.secret.key}") // LLAVE SECRETA
    private String wompiSecretKey;

    /**
     * Inicia una recarga de saldo con Wompi y guarda la transacción pendiente.
     * @param userId ID del usuario que quiere recargar.
     * @param amount Monto a recargar.
     * @return La transacción de recarga creada.
     */
    @Transactional
    public RechargeTransaction initiateWompiPayment(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a 0");
        }

        // Crear transacción pendiente en la DB
        RechargeTransaction transaction = new RechargeTransaction();
        transaction.setUser(user);
        transaction.setMonto(amount);
        transaction.setMetodoPago(RechargeTransaction.PaymentMethod.WOMPI);
        transaction.setEstado(RechargeTransaction.TransactionStatus.PENDING);
        // Generar una referencia única para Wompi y la transacción local
        String wompiReference = "RECHARGA_" + user.getId() + "_" + System.currentTimeMillis();
        transaction.setReferenciaExterna(wompiReference); // Este será el 'reference' para Wompi
        transaction.setFechaCreacion(LocalDateTime.now());
        transaction.setFechaActualizacion(LocalDateTime.now());

        return rechargeTransactionRepository.save(transaction);
    }

    /**
     * Genera el HTML del formulario de Web Checkout de Wompi.
     * @param transaction La transacción ya guardada en la base de datos.
     * @param user El usuario que realiza la recarga.
     * @return El HTML del formulario.
     */
    public String generateWompiWebCheckoutForm(RechargeTransaction transaction, User user) {
        try {
            String wompiReference = transaction.getReferenciaExterna(); // Usar la referencia generada por nosotros
            BigDecimal amount = transaction.getMonto();
            String customerEmail = user.getEmail();
            String customerFullName = user.getNombreCompleto();

            // 1. Calcular el monto en centavos
            int amountInCents = amount.multiply(BigDecimal.valueOf(100)).intValue();

            // 2. Generar la firma de integridad (esto es crítico y debe hacerse con la SECRET KEY del comercio en el backend)
            String integritySignature = generateIntegritySignature(wompiReference, amountInCents, wompiSecretKey);

            // 3. Construir el HTML del formulario
            return String.format(
                    """
                    <form action="https://checkout.wompi.co/p/" method="GET">
                      <input type="hidden" name="public-key" value="%s" />
                      <input type="hidden" name="currency" value="COP" />
                      <input type="hidden" name="amount-in-cents" value="%d" />
                      <input type="hidden" name="reference" value="%s" />
                      <input type="hidden" name="signature:integrity" value="%s" />
                      <input type="hidden" name="customer-data:email" value="%s" />
                      <input type="hidden" name="customer-data:full-name" value="%s" />
                      <!-- Puedes añadir más campos ocultos según sea necesario -->
                      <button type="submit" class="w-full bg-gradient-to-r from-green-600 to-teal-600 text-white py-3 rounded-lg font-semibold hover:from-green-700 hover:to-teal-700 transition-all duration-300 flex items-center justify-center space-x-2">
                        <span>Pagar con Wompi</span>
                      </button>
                    </form>
                    """,
                    wompiPublicKey, // LLAVE PÚBLICA
                    amountInCents, // MONTO EN CENTAVOS
                    wompiReference, // REFERENCIA DE PAGO (generada por ti)
                    integritySignature, // FIRMA DE INTEGRIDAD (generada por ti con la SECRET KEY)
                    customerEmail, // CORREO DEL PAGADOR
                    customerFullName // NOMBRE DEL PAGADOR
            );
        } catch (Exception e) {
            log.error("Error generando formulario de Web Checkout", e);
            throw new RuntimeException("Error generando formulario de pago", e);
        }
    }

    /**
     * Genera la firma de integridad para el Web Checkout de Wompi.
     * @param reference Referencia del pago.
     * @param amountInCents Monto en centavos.
     * @param secretKey Clave secreta de Wompi.
     * @return La firma codificada en Base64.
     */
    private String generateIntegritySignature(String reference, int amountInCents, String secretKey) {
        // El mensaje para firmar debe seguir el formato: reference + amount_in_cents
        // Ej: "RECHARGA_1_1703123456789" + "25000"
        String message = reference + amountInCents;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generando firma de integridad", e);
            throw new RuntimeException("Error interno al generar la firma de pago.", e);
        }
    }

    /**
     * Maneja el webhook de Wompi.
     * @param wompiPaymentIntentId ID de la transacción en Wompi.
     * @param status Estado reportado por Wompi (e.g., APPROVED, DECLINED).
     */
    @Transactional
    public void handleWompiWebhook(String wompiPaymentIntentId, String status) {
        log.info("Recibido webhook de Wompi para ID: {}, Status: {}", wompiPaymentIntentId, status);

        RechargeTransaction transaction = rechargeTransactionRepository.findByReferenciaExterna(wompiPaymentIntentId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada para ID de Wompi: " + wompiPaymentIntentId));

        if ("APPROVED".equalsIgnoreCase(status)) {
            // APROBAR: Sumar el monto al saldo del usuario
            User user = transaction.getUser();
            user.setSaldo(user.getSaldo().add(transaction.getMonto()));
            userRepository.save(user);

            // Actualizar estado de la transacción
            transaction.setEstado(RechargeTransaction.TransactionStatus.COMPLETED);
            transaction.setFechaActualizacion(LocalDateTime.now());
            rechargeTransactionRepository.save(transaction);

            log.info("Recarga completada para usuario ID: {}, monto: {}, transacción: {}", user.getId(), transaction.getMonto(), transaction.getId());

        } else if ("DECLINED".equalsIgnoreCase(status) || "EXPIRED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
            // RECHAZAR/EXPIRAR/CANCELAR: Marcar como fallida
            transaction.setEstado(RechargeTransaction.TransactionStatus.FAILED);
            transaction.setFechaActualizacion(LocalDateTime.now());
            rechargeTransactionRepository.save(transaction);
            log.info("Recarga fallida/cancelada para ID Wompi: {}, estado: {}, transacción: {}", wompiPaymentIntentId, status, transaction.getId());

        } else {
            log.warn("Estado de transacción Wompi no manejado: {} para ID: {}, transacción: {}", status, wompiPaymentIntentId, transaction.getId());
        }
    }

    /**
     * Procesa una compra usando el saldo del usuario.
     * @param userId ID del usuario que compra.
     * @param cartItems Items del carrito (lista de mapas con 'toolId' y 'quantity').
     * @throws RuntimeException Si el saldo es insuficiente o hay errores.
     */
    @Transactional
    public void processPurchaseWithBalance(Long userId, List<Map<String, Object>> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Calcular el total de la compra (usando precio público del Tool)
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<UserPurchaseWithBalance> purchasesToSave = new java.util.ArrayList<>();

        for (Map<String, Object> item : cartItems) {
            Long toolId = Long.parseLong(item.get("toolId").toString());
            Integer quantity = Integer.parseInt(item.get("quantity").toString());

            Tool tool = toolRepository.findById(toolId)
                    .orElseThrow(() -> new RuntimeException("Herramienta no encontrada: " + toolId));

            // Usar el precio público del Tool para la compra
            BigDecimal itemTotal = tool.getPrecio().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);

            // Crear registro de compra
            UserPurchaseWithBalance purchase = new UserPurchaseWithBalance();
            purchase.setUser(user);
            purchase.setTool(tool);
            purchase.setCantidad(quantity);
            purchase.setPrecioUnitario(tool.getPrecio()); // Precio público
            purchase.setPrecioTotal(itemTotal);
            purchase.setFechaCompra(LocalDateTime.now());
            purchasesToSave.add(purchase);
        }

        // Verificar saldo
        if (user.getSaldo().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Saldo insuficiente. Saldo actual: " + user.getSaldo() + ", Total de la compra: " + totalAmount);
        }

        // Descontar saldo
        user.setSaldo(user.getSaldo().subtract(totalAmount));
        userRepository.save(user);

        // Guardar registros de compra
        userPurchaseWithBalanceRepository.saveAll(purchasesToSave);

        log.info("Compra procesada con saldo para usuario ID: {}. Total descontado: {}", user.getId(), totalAmount);
    }
}