// src/main/java/com/ia.platform.ia_platform_backend/service/DatabaseInitializerService.java

package com.ia.platform.ia_platform_backend.service;

import com.ia.platform.ia_platform_backend.entity.Category;
import com.ia.platform.ia_platform_backend.entity.Role;
import com.ia.platform.ia_platform_backend.entity.User;
import com.ia.platform.ia_platform_backend.repository.CategoryRepository;
import com.ia.platform.ia_platform_backend.repository.RoleRepository;
import com.ia.platform.ia_platform_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DatabaseInitializerService implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeCategories();
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            Category combo = new Category();
            combo.setNombre("combo");
            combo.setDescripcion("Paquetes que incluyen múltiples herramientas");
            categoryRepository.save(combo);

            Category ai = new Category();
            ai.setNombre("ai");
            ai.setDescripcion("Herramientas de inteligencia artificial");
            categoryRepository.save(ai);

            Category productivity = new Category();
            productivity.setNombre("productivity");
            productivity.setDescripcion("Herramientas de productividad");
            categoryRepository.save(productivity);

            Category education = new Category();
            education.setNombre("education");
            education.setDescripcion("Herramientas educativas");
            categoryRepository.save(education);

            Category design = new Category();
            design.setNombre("design");
            design.setDescripcion("Herramientas de diseño");
            categoryRepository.save(design);

            Category video = new Category();
            video.setNombre("video");
            video.setDescripcion("Herramientas de edición de video");
            categoryRepository.save(video);
        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            // Cliente
            Role cliente = new Role();
            cliente.setNombre("cliente");
            cliente.setDescripcion("Usuario final que compra herramientas");
            roleRepository.save(cliente);

            // Revendedor
            Role revendedor = new Role();
            revendedor.setNombre("revendedor");
            revendedor.setDescripcion("Usuario que puede vender herramientas con descuento");
            roleRepository.save(revendedor);

            // Administrador
            Role administrador = new Role();
            administrador.setNombre("administrador");
            administrador.setDescripcion("Usuario con todos los permisos de administración");
            roleRepository.save(administrador);

            // Asesor
            Role asesor = new Role();
            asesor.setNombre("asesor");
            asesor.setDescripcion("Usuario que asesora a clientes y revendedores");
            roleRepository.save(asesor);
        }
    }

    private void initializeAdminUser() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByNombre("administrador")
                    .orElseThrow(() -> new RuntimeException("Rol administrador no encontrado"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@ia-platform.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña: admin123
            admin.setNombreCompleto("Administrador del Sistema");
            admin.setEstado("activo");
            admin.setFechaRegistro(LocalDateTime.now());
            admin.setRole(adminRole);
            userRepository.save(admin);
        }
    }
}