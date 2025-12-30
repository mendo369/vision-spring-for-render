// src/main/java/com/ia/platform/ia_platform_backend/repository/RechargeTransactionRepository.java

package com.ia.platform.ia_platform_backend.repository;

import com.ia.platform.ia_platform_backend.dto.RechargeTransactionBasicDTO;
import com.ia.platform.ia_platform_backend.entity.RechargeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RechargeTransactionRepository extends JpaRepository<RechargeTransaction, Long> {
    Optional<RechargeTransaction> findByReferenciaExterna(String referenciaExterna);
    @Query("SELECT new com.ia.platform.ia_platform_backend.dto.RechargeTransactionBasicDTO(" +
            "rt.id, rt.monto, " +
            "CAST(rt.metodoPago AS string), " +
            "CAST(rt.estado AS string), " +
            "rt.referenciaExterna, " +
            "rt.fechaCreacion, rt.fechaActualizacion) " +
            "FROM RechargeTransaction rt WHERE rt.user.id = :userId")
    List<RechargeTransactionBasicDTO> findBasicByUserId(@Param("userId") Long userId);
}