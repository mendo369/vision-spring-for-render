// src/main/java/com/ia.platform.ia_platform_backend/service/UserService.java

package com.ia.platform.ia_platform_backend.service;

import com.ia.platform.ia_platform_backend.dto.UserDto;
import com.ia.platform.ia_platform_backend.entity.Role;
import com.ia.platform.ia_platform_backend.entity.User;
import com.ia.platform.ia_platform_backend.repository.RoleRepository;
import com.ia.platform.ia_platform_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(com.ia.platform.ia_platform_backend.dto.RegisterRequest request, String roleName) {
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
        user.setFechaRegistro(LocalDateTime.now());

        Role role = roleRepository.findByNombre(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
        user.setRole(role);

        user = userRepository.save(user);
        return mapToDto(user);
    }

    public List<UserDto> getUsersByRole(String roleName) {
        Role role = roleRepository.findByNombre(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));

        List<User> users = userRepository.findByRole(role);
        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private UserDto mapToDto(User user) {
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
}