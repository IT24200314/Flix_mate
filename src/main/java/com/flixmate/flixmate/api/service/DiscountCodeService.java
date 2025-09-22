package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.DiscountCode;
import com.flixmate.flixmate.api.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService {
    
    @Autowired
    private DiscountCodeRepository discountCodeRepository;
    
    public Optional<DiscountCode> validateDiscountCode(String code, Double purchaseAmount) {
        return discountCodeRepository.findValidByCode(code, LocalDateTime.now())
                .filter(discountCode -> purchaseAmount >= discountCode.getMinPurchaseAmount());
    }
    
    public Double calculateDiscount(String code, Double purchaseAmount) {
        Optional<DiscountCode> discountCodeOpt = validateDiscountCode(code, purchaseAmount);
        if (discountCodeOpt.isPresent()) {
            return discountCodeOpt.get().calculateDiscount(purchaseAmount);
        }
        return 0.0;
    }
    
    public boolean applyDiscountCode(String code, Double purchaseAmount) {
        Optional<DiscountCode> discountCodeOpt = validateDiscountCode(code, purchaseAmount);
        if (discountCodeOpt.isPresent()) {
            DiscountCode discountCode = discountCodeOpt.get();
            discountCode.incrementUsage();
            discountCodeRepository.save(discountCode);
            return true;
        }
        return false;
    }
    
    public List<DiscountCode> getActiveDiscountCodes() {
        return discountCodeRepository.findByIsActiveTrueOrderByValidFromDesc();
    }
    
    public DiscountCode createDiscountCode(String code, String description, String discountType, 
                                         Double discountValue, Double minPurchaseAmount, 
                                         Integer usageLimit, Integer validDays) {
        DiscountCode discountCode = new DiscountCode(code, description, discountType, discountValue);
        discountCode.setMinPurchaseAmount(minPurchaseAmount);
        discountCode.setUsageLimit(usageLimit);
        discountCode.setValidFrom(LocalDateTime.now());
        discountCode.setValidUntil(LocalDateTime.now().plusDays(validDays));
        
        return discountCodeRepository.save(discountCode);
    }
}
