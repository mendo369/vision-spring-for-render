// src/main/java/com/ia.platform.ia_platform_backend/dto/AuthResponse.java

package com.ia.platform.ia_platform_backend.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserDto user;

    public AuthResponse(String token, String refreshToken, UserDto user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}