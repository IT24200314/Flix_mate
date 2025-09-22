package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_codes")
public class DiscountCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Integer codeId;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "discount_type", nullable = false)
    private String discountType; // PERCENTAGE or FIXED_AMOUNT
    
    @Column(name = "discount_value", nullable = false)
    private Double discountValue;
    
    @Column(name = "min_purchase_amount")
    private Double minPurchaseAmount = 0.0;
    
    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public DiscountCode() {}
    
    public DiscountCode(String code, String description, String discountType, Double discountValue) {
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.validFrom = LocalDateTime.now();
        this.validUntil = LocalDateTime.now().plusMonths(1);
    }
    
    // Getters and Setters
    public Integer getCodeId() { return codeId; }
    public void setCodeId(Integer codeId) { this.codeId = codeId; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    
    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }
    
    public Double getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }
    
    public Double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(Double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }
    
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    // Helper methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               now.isAfter(validFrom) && 
               now.isBefore(validUntil) &&
               (usageLimit == null || usedCount < usageLimit);
    }
    
    public Double calculateDiscount(Double purchaseAmount) {
        if (!isValid() || purchaseAmount < minPurchaseAmount) {
            return 0.0;
        }
        
        Double discount = 0.0;
        if ("PERCENTAGE".equals(discountType)) {
            discount = purchaseAmount * (discountValue / 100.0);
        } else if ("FIXED_AMOUNT".equals(discountType)) {
            discount = discountValue;
        }
        
        if (maxDiscountAmount != null && discount > maxDiscountAmount) {
            discount = maxDiscountAmount;
        }
        
        return Math.min(discount, purchaseAmount);
    }
    
    public void incrementUsage() {
        this.usedCount++;
    }
}
