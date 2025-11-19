// Tool.java
package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "herramientas")
@Data
public class Tool extends BaseEntity {
    private String nombre;
    private String descripcionCorta;
    @Lob
    private String descripcionLarga;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(name = "precio_revendedor", precision = 12, scale = 2)
    private BigDecimal precioRevendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category categoria;

    private String imagenUrl;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "activo";

    @Column(name = "es_mas_vendido")
    private Boolean esMasVendido = false;

    @Column(name = "es_mas_popular")
    private Boolean esMasPopular = false;

    @OneToMany(mappedBy = "herramienta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ToolFeature> caracteristicas;

    public boolean isActivo() {
        return "activo".equalsIgnoreCase(estado);
    }

    public BigDecimal getPrecioFinal(boolean isReseller) {
        if (isReseller && precioRevendedor != null) {
            return precioRevendedor;
        }
        return precio;
    }
}