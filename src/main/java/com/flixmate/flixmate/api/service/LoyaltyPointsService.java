package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.LoyaltyPoints;
import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.repository.LoyaltyPointsRepository;
import com.flixmate.flixmate.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoyaltyPointsService {
    
    @Autowired
    private LoyaltyPointsRepository loyaltyPointsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final int POINTS_PER_DOLLAR = 10; // 10 points per $1 spent
    private static final int POINTS_FOR_REDEMPTION = 100; // 100 points = $1 discount
    
    public LoyaltyPoints getOrCreateLoyaltyPoints(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return loyaltyPointsRepository.findByUser(user)
                .orElseGet(() -> {
                    LoyaltyPoints newPoints = new LoyaltyPoints(user);
                    return loyaltyPointsRepository.save(newPoints);
                });
    }
    
    public LoyaltyPoints getLoyaltyPoints(String email) {
        return getOrCreateLoyaltyPoints(email);
    }
    
    public void addPointsForPurchase(String email, Double purchaseAmount) {
        LoyaltyPoints loyaltyPoints = getOrCreateLoyaltyPoints(email);
        int pointsToAdd = (int) (purchaseAmount * POINTS_PER_DOLLAR);
        loyaltyPoints.addPoints(pointsToAdd);
        loyaltyPointsRepository.save(loyaltyPoints);
    }
    
    public boolean redeemPoints(String email, Integer pointsToRedeem) {
        LoyaltyPoints loyaltyPoints = getOrCreateLoyaltyPoints(email);
        boolean success = loyaltyPoints.redeemPoints(pointsToRedeem);
        if (success) {
            loyaltyPointsRepository.save(loyaltyPoints);
        }
        return success;
    }
    
    public Double calculateDiscountFromPoints(String email, Integer pointsToRedeem) {
        if (pointsToRedeem < POINTS_FOR_REDEMPTION) {
            return 0.0;
        }
        return (double) (pointsToRedeem / POINTS_FOR_REDEMPTION);
    }
    
    public Integer getMaxRedeemablePoints(String email) {
        LoyaltyPoints loyaltyPoints = getOrCreateLoyaltyPoints(email);
        return (loyaltyPoints.getPointsBalance() / POINTS_FOR_REDEMPTION) * POINTS_FOR_REDEMPTION;
    }
}
