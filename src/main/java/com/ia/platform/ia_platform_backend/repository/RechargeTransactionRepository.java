// src/main/java/com/ia/platform/ia_platform_backend/repository/RechargeTransactionRepository.java

package com.ia.platform.ia_platform_backend.repository;

import com.ia.platform.ia_platform_backend.entity.RechargeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RechargeTransactionRepository extends JpaRepository<RechargeTransaction, Long> {
    Optional<RechargeTransaction> findByReferenciaExterna(String referenciaExterna);
}