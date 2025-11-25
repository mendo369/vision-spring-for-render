// src/main/java/com/ia/platform/ia_platform_backend/entity/UserPurchaseWithBalance.java

package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras_usuario_con_saldo")
@Data
public class UserPurchaseWithBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool; // Asumiendo que tienes una entidad Tool

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false) // Precio público en el momento de la compra
    private BigDecimal precioUnitario;

    @Column(name = "precio_total", precision = 10, scale = 2, nullable = false) // precio_unitario * cantidad
    private BigDecimal precioTotal;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra = LocalDateTime.now();

    // Opcional: Referencia de la transacción si aplica
    // @Column(name = "referencia_pago")
    // private String referenciaPago;
}