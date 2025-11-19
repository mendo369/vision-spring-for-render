// src/main/java/com/ia.platform.ia_platform_backend/repository/ToolRepository.java

package com.ia.platform.ia_platform_backend.repository;

import com.ia.platform.ia_platform_backend.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    List<Tool> findByEstado(String estado);
    List<Tool> findByEsMasVendidoTrue();
    List<Tool> findByEsMasPopularTrue();
}