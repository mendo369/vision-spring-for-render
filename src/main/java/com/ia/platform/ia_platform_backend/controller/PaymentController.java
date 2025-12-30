// src/main/java/com/ia/platform/ia_platform_backend/controller/PaymentController.java

package com.ia.platform.ia_platform_backend.controller;

import com.ia.platform.ia_platform_backend.dto.RechargeRequest;
import com.ia.platform.ia_platform_backend.dto.RechargeTransactionBasicDTO;
import com.ia.platform.ia_platform_backend.entity.RechargeTransaction;
import com.ia.platform.ia_platform_backend.entity.User;
import com.ia.platform.ia_platform_backend.repository.RechargeTransactionRepository;
import com.ia.platform.ia_platform_backend.repository.UserRepository;
import com.ia.platform.ia_platform_backend.security.JwtService;
import com.ia.platform.ia_platform_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Ajusta según sea necesario
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository; // Inyectar UserRepository
    private final RechargeTransactionRepository transactionRepository;
    private final JwtService jwtService;

    // Endpoint para iniciar una recarga de saldo con Wompi
    // En PaymentController.java
    // En PaymentController.java
    @PostMapping("/wompi/iniciar")
    public ResponseEntity<Map<String, Object>> iniciarRecargaWompi(@RequestBody RechargeRequest request,
                                                                   @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        if (username == null || username.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Token inválido o sin username");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal amount = new BigDecimal(request.getMonto());

            // Buscar el usuario por username en lugar de usar ID fijo
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

            RechargeTransaction transaction = paymentService.initiateWompiPayment(user.getId(), amount);

            String formHtml = paymentService.generateWompiWebCheckoutForm(transaction, user);

            response.put("success", true);
            response.put("message", "Pago iniciado");
            response.put("wompi_payment_form_html", formHtml);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint para el Webhook de Wompi (debes configurarlo en el panel de Wompi)
    @PostMapping("/webhooks/wompi")
    public ResponseEntity<String> handleWompiWebhook(@RequestBody Map<String, Object> webhookData) {
        log.info("Webhook recibido: {}", webhookData);

        try {
            // Extraer el tipo de evento
            String eventType = (String) webhookData.get("event");

            if (!"transaction.updated".equals(eventType)) {
                log.info("Evento no manejado: {}", eventType);
                return ResponseEntity.ok("OK"); // Aceptar otros eventos también con 200
            }

            // Extraer la data de la transacción
            Map<String, Object> data = (Map<String, Object>) webhookData.get("data");
            Map<String, Object> transactionData = (Map<String, Object>) data.get("transaction");

            String wompiTransactionId = (String) transactionData.get("id");
            String status = (String) transactionData.get("status");
            String reference = (String) transactionData.get("reference"); // 👈 Esta es la referencia que tú enviaste

            log.info("Procesando webhook: Evento={}, ID={}, Status={}, Reference={}", eventType, wompiTransactionId, status, reference);

            // Usar la referencia para buscar la transacción en tu base de datos
            paymentService.handleWompiWebhookByReference(reference, status);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error procesando webhook de Wompi: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    // Endpoint para procesar compra con saldo
    @PostMapping("/procesar-con-saldo")
    public ResponseEntity<Map<String, Object>> procesarCompraConSaldo(@RequestBody Map<String, Object> request,
                                                                      @RequestHeader("Authorization") String authHeader) { // Obtener usuario logueado
        // Long userId = extractUserIdFromToken(authHeader);
        Long userId = 1L; // Simulación

        Map<String, Object> response = new HashMap<>();
        try {
            // El request debe contener cartItems
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> cartItems = (java.util.List<Map<String, Object>>) request.get("cartItems");

            paymentService.processPurchaseWithBalance(userId, cartItems);

            response.put("success", true);
            response.put("message", "Compra procesada exitosamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint para obtener el saldo del usuario logueado
    @GetMapping("/saldo")
    public ResponseEntity<Map<String, Object>> getUserBalance(@RequestHeader("Authorization") String authHeader) {
        // Long userId = extractUserIdFromToken(authHeader);
        Long userId = 1L; // Simulación

        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            response.put("saldo", user.getSaldo());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getUserRecharges(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);

        Map<String, Object> response = new HashMap<>();

        try {
            List<RechargeTransactionBasicDTO> data = transactionRepository.findBasicByUserId(userId);

            response.put("success", true);
            response.put("message", "Historial obtenido.");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}