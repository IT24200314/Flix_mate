package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotional_banners")
public class PromotionalBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Integer bannerId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "target_url")
    private String targetUrl;

    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "start_date", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "click_count")
    private Integer clickCount = 0;

    // Getters, setters, constructors
    public Integer getBannerId() { return bannerId; }
    public void setBannerId(Integer bannerId) { this.bannerId = bannerId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    
    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }

    public PromotionalBanner() {}
    
    public PromotionalBanner(String title, String description, String imageUrl, 
                           LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
