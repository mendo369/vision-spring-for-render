// src/main/java/com/ia.platform.ia_platform_backend/controller/AuthController.java

package com.ia.platform.ia_platform_backend.controller;

import com.ia.platform.ia_platform_backend.dto.AuthRequest;
import com.ia.platform.ia_platform_backend.dto.AuthResponse;
import com.ia.platform.ia_platform_backend.dto.RegisterRequest;
import com.ia.platform.ia_platform_backend.dto.UserDto;
import com.ia.platform.ia_platform_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto userDto = authService.registerUser(request);
        return ResponseEntity.ok(userDto);
    }
}