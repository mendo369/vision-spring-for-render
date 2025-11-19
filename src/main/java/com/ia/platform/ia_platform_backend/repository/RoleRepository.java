// src/main/java/com/ia.platform.ia_platform_backend/repository/RoleRepository.java

package com.ia.platform.ia_platform_backend.repository;

import com.ia.platform.ia_platform_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombre(String nombre);
}