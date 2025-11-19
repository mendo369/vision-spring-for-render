// src/main/java/com/ia.platform.ia_platform_backend/repository/UserRepository.java

package com.ia.platform.ia_platform_backend.repository;

import com.ia.platform.ia_platform_backend.entity.Role;
import com.ia.platform.ia_platform_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role); // Añadimos este método
}