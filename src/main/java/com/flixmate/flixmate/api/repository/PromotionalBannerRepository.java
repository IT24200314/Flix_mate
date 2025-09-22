package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.PromotionalBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionalBannerRepository extends JpaRepository<PromotionalBanner, Integer> {

    @Query("SELECT b FROM PromotionalBanner b WHERE b.isActive = true AND " +
           "b.startDate <= :currentTime AND b.endDate >= :currentTime " +
           "ORDER BY b.displayOrder ASC")
    List<PromotionalBanner> findActiveBanners(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM PromotionalBanner b WHERE b.isActive = true " +
           "ORDER BY b.displayOrder ASC")
    List<PromotionalBanner> findAllActiveBanners();

    List<PromotionalBanner> findByIsActiveTrueOrderByDisplayOrderAsc();
}
