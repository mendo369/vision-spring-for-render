// src/main/java/com/ia.platform.ia_platform_backend/dto/CreateToolRequest.java

package com.ia.platform.ia_platform_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateToolRequest {
    private String nombre;
    private String descripcionCorta;
    private String descripcionLarga;
    private BigDecimal precio;
    private BigDecimal precioRevendedor;
    private String categoria; // Nombre de la categoría
    private String imagenUrl;
    private Boolean esMasVendido = false;
    private Boolean esMasPopular = false;
    private List<String> caracteristicas;
}