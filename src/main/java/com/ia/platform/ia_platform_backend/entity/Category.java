// Category.java
package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categorias")
@Data
public class Category extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;
}