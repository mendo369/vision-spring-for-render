// src/main/java/com/ia.platform.ia_platform_backend/entity/Role.java

package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Data
public class Role extends BaseEntity implements GrantedAuthority {
    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;

    @Override
    public String getAuthority() {
        return nombre;
    }
}