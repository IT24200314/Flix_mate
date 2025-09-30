package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.PromotionalBanner;
import com.flixmate.flixmate.api.service.PromotionalBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class PromotionalBannerController {

    @Autowired
    private PromotionalBannerService bannerService;

    @GetMapping
    public ResponseEntity<?> getActiveBanners() {
        try {
            List<PromotionalBanner> banners = bannerService.getActiveBanners();
            return ResponseEntity.ok(banners);
        } catch (Exception e) {
            System.err.println("Error fetching active banners: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch promotional banners: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllBanners() {
        try {
            List<PromotionalBanner> banners = bannerService.getAllBanners();
            return ResponseEntity.ok(banners);
        } catch (Exception e) {
            System.err.println("Error fetching all banners: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch all banners: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBannerById(@PathVariable Integer id) {
        try {
            PromotionalBanner banner = bannerService.getBannerById(id);
            if (banner != null) {
                return ResponseEntity.ok(banner);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching banner by ID: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch banner: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBanner(@RequestBody PromotionalBanner banner) {
        try {
            PromotionalBanner createdBanner = bannerService.createBanner(banner);
            return ResponseEntity.ok(createdBanner);
        } catch (Exception e) {
            System.err.println("Error creating banner: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to create banner: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBanner(@PathVariable Integer id, @RequestBody PromotionalBanner banner) {
        try {
            PromotionalBanner updatedBanner = bannerService.updateBanner(id, banner);
            return ResponseEntity.ok(updatedBanner);
        } catch (Exception e) {
            System.err.println("Error updating banner: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to update banner: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBanner(@PathVariable Integer id) {
        try {
            boolean deleted = bannerService.deleteBanner(id);
            if (deleted) {
                return ResponseEntity.ok("Banner deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting banner: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to delete banner: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<?> incrementClickCount(@PathVariable Integer id) {
        try {
            bannerService.incrementClickCount(id);
            return ResponseEntity.ok("Click count incremented");
        } catch (Exception e) {
            System.err.println("Error incrementing click count: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to track click: " + e.getMessage());
        }
    }
}
