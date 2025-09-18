package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.DiscountCode;
import com.flixmate.flixmate.api.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountCodeController {
    
    @Autowired
    private DiscountCodeService discountCodeService;
    
    @GetMapping("/validate")
    public ResponseEntity<Double> validateDiscountCode(@RequestParam String code, 
                                                     @RequestParam Double amount) {
        try {
            Double discount = discountCodeService.calculateDiscount(code, amount);
            return ResponseEntity.ok(discount);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(0.0);
        }
    }
    
    @PostMapping("/apply")
    public ResponseEntity<String> applyDiscountCode(@RequestParam String code, 
                                                  @RequestParam Double amount) {
        try {
            boolean success = discountCodeService.applyDiscountCode(code, amount);
            if (success) {
                return ResponseEntity.ok("Discount code applied successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired discount code");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error applying discount code: " + ex.getMessage());
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<DiscountCode>> getActiveDiscountCodes() {
        try {
            List<DiscountCode> codes = discountCodeService.getActiveDiscountCodes();
            return ResponseEntity.ok(codes);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
