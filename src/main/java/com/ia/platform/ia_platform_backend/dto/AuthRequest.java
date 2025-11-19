// src/main/java/com/ia.platform.ia_platform_backend/dto/AuthRequest.java

package com.ia.platform.ia_platform_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}