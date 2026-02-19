package com.smartmobility.usermobilitypassservice.repository;

import com.smartmobility.usermobilitypassservice.entity.MobilityPass;
import com.smartmobility.usermobilitypassservice.entity.PassStatus;
import com.smartmobility.usermobilitypassservice.entity.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MobilityPassRepository extends JpaRepository<MobilityPass, UUID> {

    Optional<MobilityPass> findByPassNumber(String passNumber);

    Optional<MobilityPass> findByUserId(UUID userId);

    boolean existsByPassNumber(String passNumber);

    List<MobilityPass> findByStatus(PassStatus status);

    List<MobilityPass> findBySubscriptionType(SubscriptionType subscriptionType);

    @Query("SELECT mp FROM MobilityPass mp WHERE mp.balance < :threshold AND mp.status = :status")
    List<MobilityPass> findByBalanceLessThanAndStatus(@Param("threshold") BigDecimal threshold, @Param("status") PassStatus status);

    @Query("SELECT mp FROM MobilityPass mp WHERE mp.subscriptionEndDate < CURRENT_DATE AND mp.status = 'ACTIVE'")
    List<MobilityPass> findExpiredSubscriptions();
}