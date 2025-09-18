package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {
    Optional<DiscountCode> findByCode(String code);
    
    @Query("SELECT d FROM DiscountCode d WHERE d.code = :code AND d.isActive = true AND d.validFrom <= :now AND d.validUntil >= :now AND (d.usageLimit IS NULL OR d.usedCount < d.usageLimit)")
    Optional<DiscountCode> findValidByCode(@Param("code") String code, @Param("now") LocalDateTime now);
    
    List<DiscountCode> findByIsActiveTrueOrderByValidFromDesc();
}
