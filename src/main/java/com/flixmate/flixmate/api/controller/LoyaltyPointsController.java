package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.LoyaltyPoints;
import com.flixmate.flixmate.api.service.LoyaltyPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyPointsController {
    
    @Autowired
    private LoyaltyPointsService loyaltyPointsService;
    
    @GetMapping("/points")
    public ResponseEntity<LoyaltyPoints> getLoyaltyPoints(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            LoyaltyPoints loyaltyPoints = loyaltyPointsService.getLoyaltyPoints(userDetails.getUsername());
            return ResponseEntity.ok(loyaltyPoints);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/redeem")
    public ResponseEntity<String> redeemPoints(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam Integer points) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            boolean success = loyaltyPointsService.redeemPoints(userDetails.getUsername(), points);
            if (success) {
                return ResponseEntity.ok("Points redeemed successfully");
            } else {
                return ResponseEntity.badRequest().body("Insufficient points or invalid amount");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error redeeming points: " + ex.getMessage());
        }
    }
    
    @GetMapping("/max-redeemable")
    public ResponseEntity<Integer> getMaxRedeemablePoints(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            Integer maxPoints = loyaltyPointsService.getMaxRedeemablePoints(userDetails.getUsername());
            return ResponseEntity.ok(maxPoints);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
