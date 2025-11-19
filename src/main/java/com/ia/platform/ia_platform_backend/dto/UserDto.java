// src/main/java/com/ia.platform.ia_platform_backend/dto/UserDto.java

package com.ia.platform.ia_platform_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String telefono;
    private String direccion;
    private String roleName;
    private String estado;
    private LocalDateTime fechaRegistro;
}