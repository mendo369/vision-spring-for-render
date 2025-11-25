// User.java
package com.ia.platform.ia_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombreCompleto;
    private String telefono;
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "activo";

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    // --- Campo para el saldo ---
    @Column(name = "saldo", precision = 10, scale = 2, nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal saldo = BigDecimal.ZERO; // Inicializar en 0

    // Implementación de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role != null ? java.util.Collections.singletonList(role) : java.util.Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return "activo".equalsIgnoreCase(estado); }

    public boolean isActive() {
        return "activo".equalsIgnoreCase(estado);
    }
}