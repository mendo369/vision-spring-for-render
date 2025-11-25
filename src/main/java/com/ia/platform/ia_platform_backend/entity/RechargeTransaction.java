// src/main/java/com/ia/platform/ia_platform_backend/entity/RechargeTransaction.java

package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recargas_saldo")
@Data
public class RechargeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private PaymentMethod metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private TransactionStatus estado;

    @Column(name = "referencia_externa", length = 255, unique = true) // ID de la transacción en Wompi
    private String referenciaExterna;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public enum PaymentMethod {
        MERCADOPAGO, PSE, CARD, EFECTY, WOMPI // Añadir WOMPI
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
}