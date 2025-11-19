// src/main/java/com/ia.platform.ia_platform_backend/controller/UserController.java

package com.ia.platform.ia_platform_backend.controller;

import com.ia.platform.ia_platform_backend.dto.RegisterRequest;
import com.ia.platform.ia_platform_backend.dto.UserDto;
import com.ia.platform.ia_platform_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    // Crear usuario cliente
    @PostMapping("/cliente")
    public ResponseEntity<UserDto> createCliente(@RequestBody RegisterRequest request) {
        UserDto userDto = userService.createUser(request, "cliente");
        return ResponseEntity.ok(userDto);
    }

    // Crear usuario revendedor
    @PostMapping("/revendedor")
    public ResponseEntity<UserDto> createRevendedor(@RequestBody RegisterRequest request) {
        UserDto userDto = userService.createUser(request, "revendedor");
        return ResponseEntity.ok(userDto);
    }

    // Crear usuario asesor
    @PostMapping("/asesor")
    public ResponseEntity<UserDto> createAsesor(@RequestBody RegisterRequest request) {
        UserDto userDto = userService.createUser(request, "asesor");
        return ResponseEntity.ok(userDto);
    }

    // Crear usuario administrador (solo para uso interno)
    @PostMapping("/admin")
    public ResponseEntity<UserDto> createAdmin(@RequestBody RegisterRequest request) {
        UserDto userDto = userService.createUser(request, "administrador");
        return ResponseEntity.ok(userDto);
    }

    // Obtener usuarios por rol
    @GetMapping("/rol/{rolNombre}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String rolNombre) {
        List<UserDto> users = userService.getUsersByRole(rolNombre);
        return ResponseEntity.ok(users);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}