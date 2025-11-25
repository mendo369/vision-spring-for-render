// src/main/java/com/ia/platform/ia_platform_backend/repository/UserPurchaseWithBalanceRepository.java

package com.ia.platform.ia_platform_backend.repository;

import com.ia.platform.ia_platform_backend.entity.UserPurchaseWithBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserPurchaseWithBalanceRepository extends JpaRepository<UserPurchaseWithBalance, Long> {
    List<UserPurchaseWithBalance> findByUser_Id(Long userId);
}