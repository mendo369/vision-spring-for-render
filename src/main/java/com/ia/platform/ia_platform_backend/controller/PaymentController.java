// src/main/java/com/ia/platform/ia_platform_backend/controller/PaymentController.java

package com.ia.platform.ia_platform_backend.controller;

import com.ia.platform.ia_platform_backend.dto.RechargeRequest;
import com.ia.platform.ia_platform_backend.entity.RechargeTransaction;
import com.ia.platform.ia_platform_backend.entity.User;
import com.ia.platform.ia_platform_backend.repository.UserRepository;
import com.ia.platform.ia_platform_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Ajusta según sea necesario
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository; // Inyectar UserRepository

    // Endpoint para iniciar una recarga de saldo con Wompi
    @PostMapping("/wompi/iniciar")
    public ResponseEntity<Map<String, Object>> iniciarRecargaWompi(@RequestBody RechargeRequest request,
                                                                   @RequestHeader("Authorization") String authHeader) { // Obtener usuario logueado
        // Extraer user ID del token (esto depende de tu implementación de seguridad JWT)
        // Long userId = extractUserIdFromToken(authHeader);
        Long userId = 4L; // Simulación

        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal amount = new BigDecimal(request.getMonto());
            RechargeTransaction transaction = paymentService.initiateWompiPayment(userId, amount);

            // 1. Obtener el usuario para el formulario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado para generar formulario"));

            // 2. Generar el HTML del formulario de Web Checkout
            String formHtml = paymentService.generateWompiWebCheckoutForm(transaction, user);

            // 3. Devolver datos necesarios para el frontend completar el pago
            response.put("success", true);
            response.put("message", "Pago iniciado");
            response.put("wompi_payment_form_html", formHtml); // <-- Devolver el HTML
            // Opcional: también puedes devolver el ID si lo necesitas para otra cosa
            // response.put("wompi_payment_intent_id", transaction.getReferenciaExterna());
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
        try {
            // Extraer el ID de la intención de pago y el estado de la carga útil del webhook
            // La estructura exacta depende de Wompi. Ejemplo:
            Map<String, Object> data = (Map<String, Object>) webhookData.get("data");
            Map<String, Object> paymentIntent = (Map<String, Object>) data.get("payment_intent");
            String wompiPaymentIntentId = (String) paymentIntent.get("id");
            String status = (String) paymentIntent.get("status");

            // Llamar al servicio para manejar el webhook
            paymentService.handleWompiWebhook(wompiPaymentIntentId, status);

            // Devolver una respuesta 200 OK para confirmar recepción
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error procesando webhook de Wompi: ", e);
            // Wompi reintentará el webhook si no recibe un 200
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
}