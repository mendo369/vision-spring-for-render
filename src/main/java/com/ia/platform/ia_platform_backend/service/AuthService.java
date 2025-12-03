// src/main/java/com/ia.platform.ia_platform_backend/service/AuthService.java

package com.ia.platform.ia_platform_backend.service;

import com.ia.platform.ia_platform_backend.dto.AuthRequest;
import com.ia.platform.ia_platform_backend.dto.AuthResponse;
import com.ia.platform.ia_platform_backend.dto.UserDto;
import com.ia.platform.ia_platform_backend.entity.User;
import com.ia.platform.ia_platform_backend.repository.UserRepository;
import com.ia.platform.ia_platform_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // En AuthService.java
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        user.setUltimoLogin(LocalDateTime.now());
        userRepository.save(user);

        // Asegúrate de que user es un objeto User que puede ser usado por JwtService
        String jwt = jwtService.generateToken(user); // <-- Pasar el objeto User
        String refreshToken = jwtService.generateRefreshToken(user);

        UserDto userDto = mapToDto(user);

        return new AuthResponse(jwt, refreshToken, userDto);
    }

    public UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNombreCompleto(user.getNombreCompleto());
        dto.setTelefono(user.getTelefono());
        dto.setDireccion(user.getDireccion());
        dto.setRoleName(user.getRole().getNombre());
        dto.setEstado(user.getEstado());
        dto.setFechaRegistro(user.getFechaRegistro());
        return dto;
    }

    public UserDto registerUser(com.ia.platform.ia_platform_backend.dto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username ya existe");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNombreCompleto(request.getNombreCompleto());
        user.setTelefono(request.getTelefono());
        user.setDireccion(request.getDireccion());
        user.setEstado("activo");

        // Asignar rol por defecto (cliente)
        // Aquí necesitarás obtener el rol de cliente desde la base de datos
        // Por ahora, asumiremos que tienes un método para obtenerlo
        // user.setRole(roleRepository.findByNombre("cliente").orElseThrow());

        user = userRepository.save(user);
        return mapToDto(user);
    }
}