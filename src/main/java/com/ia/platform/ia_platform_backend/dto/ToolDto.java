// src/main/java/com/ia.platform.ia_platform_backend/dto/ToolDto.java

package com.ia.platform.ia_platform_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ToolDto {
    private Long id;
    private String nombre;
    private String descripcionCorta;
    private String descripcionLarga;
    private BigDecimal precio;
    private BigDecimal precioRevendedor;
    private String categoria;
    private String imagenUrl;
    private String estado;
    private Boolean esMasVendido;
    private Boolean esMasPopular;
    private List<String> caracteristicas;
}