// src/main/java/com/ia.platform.ia_platform_backend/dto/RegisterRequest.java

package com.ia.platform.ia_platform_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String nombreCompleto;
    private String telefono;
    private String direccion;
}