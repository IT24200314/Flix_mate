package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_points")
public class LoyaltyPoints {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_id")
    private Integer pointsId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "points_balance", nullable = false)
    private Integer pointsBalance = 0;
    
    @Column(name = "total_earned", nullable = false)
    private Integer totalEarned = 0;
    
    @Column(name = "total_redeemed", nullable = false)
    private Integer totalRedeemed = 0;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    // Constructors
    public LoyaltyPoints() {}
    
    public LoyaltyPoints(User user) {
        this.user = user;
        this.pointsBalance = 0;
        this.totalEarned = 0;
        this.totalRedeemed = 0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getPointsId() { return pointsId; }
    public void setPointsId(Integer pointsId) { this.pointsId = pointsId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
    
    public Integer getTotalEarned() { return totalEarned; }
    public void setTotalEarned(Integer totalEarned) { this.totalEarned = totalEarned; }
    
    public Integer getTotalRedeemed() { return totalRedeemed; }
    public void setTotalRedeemed(Integer totalRedeemed) { this.totalRedeemed = totalRedeemed; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    // Helper methods
    public void addPoints(Integer points) {
        this.pointsBalance += points;
        this.totalEarned += points;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean redeemPoints(Integer points) {
        if (this.pointsBalance >= points) {
            this.pointsBalance -= points;
            this.totalRedeemed += points;
            this.lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }
}
