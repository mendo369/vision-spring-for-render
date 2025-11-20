// src/main/java/com/ia.platform.ia_platform_backend/entity/ToolFeature.java

package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "caracteristicas_herramienta")
@Data
public class ToolFeature extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "herramienta_id", nullable = false)
    private Tool herramienta;

    @Column(nullable = false)
    private String caracteristica;

    @Column(name = "orden_display")
    private Integer ordenDisplay = 1;
}