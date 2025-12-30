// src/main/java/com/ia/platform/ia_platform_backend/dto/RechargeTransactionBasicDTO.java

package com.ia.platform.ia_platform_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeTransactionBasicDTO {
    private Long id;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String referenciaExterna;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}