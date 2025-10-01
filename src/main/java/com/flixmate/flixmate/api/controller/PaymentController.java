package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(request.getBookingId(), request.getPaymentMethod(), request.getAmount());
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        }
    }
    
    // Inner class for payment request
    public static class PaymentRequest {
        private Integer bookingId;
        private String paymentMethod;
        private Double amount;
        
        // Getters and setters
        public Integer getBookingId() { return bookingId; }
        public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentLogs() {
        return ResponseEntity.ok(paymentService.getPaymentLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable Integer id, @RequestParam String status) {
        boolean updated = paymentService.updatePaymentStatus(id, status);
        if (updated) {
            return ResponseEntity.ok("Payment status updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePayment(@PathVariable Integer id) {
        boolean deleted = paymentService.deletePayment(id);
        if (deleted) {
            return ResponseEntity.ok("Payment deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> processRefund(@PathVariable Integer id, 
                                         @RequestParam Double refundAmount,
                                         @RequestParam(required = false) String reason) {
        try {
            Payment refundedPayment = paymentService.processRefund(id, refundAmount, reason);
            return ResponseEntity.ok(refundedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Refund failed: " + e.getMessage());
        }
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserPaymentLogs(@PathVariable String email) {
        try {
            List<Payment> payments = paymentService.getPaymentLogsByUser(email);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch user payment logs: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaymentStatistics() {
        try {
            Map<String, Object> statistics = paymentService.getPaymentStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch payment statistics: " + e.getMessage());
        }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaymentsByDateRange(@RequestParam String startDate, 
                                                   @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Payment> payments = paymentService.getPaymentLogsByDateRange(start, end);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch payments by date range: " + e.getMessage());
        }
    }

    @GetMapping("/admin-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminPaymentDashboard() {
        try {
            Map<String, Object> dashboardData = paymentService.getAdminPaymentDashboard();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch admin payment dashboard: " + e.getMessage());
        }
    }

    @GetMapping("/admin-analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaymentAnalytics(@RequestParam(required = false) String startDate,
                                               @RequestParam(required = false) String endDate,
                                               @RequestParam(required = false) String status) {
        try {
            Map<String, Object> analytics = paymentService.getPaymentAnalytics(startDate, endDate, status);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch payment analytics: " + e.getMessage());
        }
    }
}
