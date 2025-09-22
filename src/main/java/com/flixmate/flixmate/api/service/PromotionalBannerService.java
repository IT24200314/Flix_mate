package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.PromotionalBanner;
import com.flixmate.flixmate.api.repository.PromotionalBannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionalBannerService {

    @Autowired
    private PromotionalBannerRepository bannerRepository;

    public List<PromotionalBanner> getActiveBanners() {
        try {
            return bannerRepository.findActiveBanners(LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error fetching active banners: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    public List<PromotionalBanner> getAllBanners() {
        try {
            return bannerRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error fetching all banners: " + e.getMessage());
            return List.of();
        }
    }

    public PromotionalBanner createBanner(PromotionalBanner banner) {
        try {
            if (banner.getStartDate() == null) {
                banner.setStartDate(LocalDateTime.now());
            }
            if (banner.getEndDate() == null) {
                banner.setEndDate(LocalDateTime.now().plusMonths(1));
            }
            return bannerRepository.save(banner);
        } catch (Exception e) {
            System.err.println("Error creating banner: " + e.getMessage());
            throw new RuntimeException("Failed to create promotional banner: " + e.getMessage());
        }
    }

    public PromotionalBanner updateBanner(Integer id, PromotionalBanner banner) {
        try {
            PromotionalBanner existingBanner = bannerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Banner not found"));
            
            existingBanner.setTitle(banner.getTitle());
            existingBanner.setDescription(banner.getDescription());
            existingBanner.setImageUrl(banner.getImageUrl());
            existingBanner.setTargetUrl(banner.getTargetUrl());
            existingBanner.setDiscountCode(banner.getDiscountCode());
            existingBanner.setDiscountPercentage(banner.getDiscountPercentage());
            existingBanner.setStartDate(banner.getStartDate());
            existingBanner.setEndDate(banner.getEndDate());
            existingBanner.setIsActive(banner.getIsActive());
            existingBanner.setDisplayOrder(banner.getDisplayOrder());
            
            return bannerRepository.save(existingBanner);
        } catch (Exception e) {
            System.err.println("Error updating banner: " + e.getMessage());
            throw new RuntimeException("Failed to update promotional banner: " + e.getMessage());
        }
    }

    public boolean deleteBanner(Integer id) {
        try {
            if (bannerRepository.existsById(id)) {
                bannerRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting banner: " + e.getMessage());
            throw new RuntimeException("Failed to delete promotional banner: " + e.getMessage());
        }
    }

    public PromotionalBanner getBannerById(Integer id) {
        try {
            return bannerRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching banner by ID: " + e.getMessage());
            return null;
        }
    }

    public void incrementClickCount(Integer id) {
        try {
            PromotionalBanner banner = bannerRepository.findById(id).orElse(null);
            if (banner != null) {
                banner.setClickCount(banner.getClickCount() + 1);
                bannerRepository.save(banner);
            }
        } catch (Exception e) {
            System.err.println("Error incrementing click count: " + e.getMessage());
            // Don't throw exception for click tracking
        }
    }
}
