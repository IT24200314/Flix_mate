package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.entity.Receipt;
import com.flixmate.flixmate.api.entity.DiscountCode;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.PaymentRepository;
import com.flixmate.flixmate.api.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ReceiptService receiptService;
    
    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    public List<Payment> getPaymentLogs() {
        return paymentRepository.findAll();
    }

    public Payment processPayment(Integer bookingId, String paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return processPayment(bookingId, paymentMethod, booking.getTotalAmount());
    }
    
    public Payment processPayment(Integer bookingId, String paymentMethod, Double amount) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Validate payment method
            if (!isValidPaymentMethod(paymentMethod)) {
                throw new RuntimeException("Invalid payment method: " + paymentMethod);
            }

            // Validate amount
            if (amount <= 0) {
                throw new RuntimeException("Invalid payment amount: " + amount);
            }

            // Mock payment gateway logic with some failure scenarios
            String transactionId = UUID.randomUUID().toString();
            LocalDateTime paymentDate = LocalDateTime.now();
            String status;
            String gatewayResponse;
            String failureReason = null;

            // Simulate different payment outcomes based on payment method
            if ("CREDIT_CARD".equals(paymentMethod)) {
                // Simulate 95% success rate for credit cards
                if (Math.random() < 0.95) {
                    status = "SUCCESS";
                    gatewayResponse = "Payment processed successfully";
                } else {
                    status = "FAILED";
                    gatewayResponse = "Payment declined";
                    failureReason = "Insufficient funds or invalid card";
                }
            } else if ("PAYPAL".equals(paymentMethod)) {
                // Simulate 95% success rate for PayPal
                if (Math.random() < 0.95) {
                    status = "SUCCESS";
                    gatewayResponse = "PayPal payment completed";
                } else {
                    status = "FAILED";
                    gatewayResponse = "PayPal payment failed";
                    failureReason = "PayPal account issue";
                }
            } else if ("E_WALLET".equals(paymentMethod)) {
                // Simulate 95% success rate for e-wallet
                if (Math.random() < 0.95) {
                    status = "SUCCESS";
                    gatewayResponse = "E-wallet payment completed";
                } else {
                    status = "FAILED";
                    gatewayResponse = "E-wallet payment failed";
                    failureReason = "Insufficient wallet balance";
                }
            } else {
                // Default to success for other methods
                status = "SUCCESS";
                gatewayResponse = "Payment processed successfully";
            }

            Payment payment = new Payment(transactionId, paymentDate, paymentMethod, amount, status, booking);
            payment.setGatewayResponse(gatewayResponse);
            payment.setFailureReason(failureReason);

            // Save payment first to get the payment ID
            Payment savedPayment = paymentRepository.save(payment);
            
            if ("SUCCESS".equals(status)) {
                booking.setStatus("CONFIRMED");
                bookingRepository.save(booking);
                
                // Generate simple receipt for successful payment
                try {
                    Receipt receipt = receiptService.generateReceipt(bookingId, savedPayment.getPaymentId());
                    System.out.println("Receipt generated successfully: " + receipt.getReceiptNumber());
                } catch (Exception receiptError) {
                    System.err.println("Failed to generate receipt: " + receiptError.getMessage());
                    receiptError.printStackTrace();
                    // Don't fail the payment for receipt generation issues
                }
            } else {
                booking.setStatus("PAYMENT_FAILED");
                bookingRepository.save(booking);
            }

            return savedPayment;
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod != null && (
            "CREDIT_CARD".equals(paymentMethod) ||
            "DEBIT_CARD".equals(paymentMethod) ||
            "PAYPAL".equals(paymentMethod) ||
            "E_WALLET".equals(paymentMethod) ||
            "BANK_TRANSFER".equals(paymentMethod)
        );
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public boolean updatePaymentStatus(Integer id, String status) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
            return true;
        }
        return false;
    }

    public boolean deletePayment(Integer id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Payment processRefund(Integer paymentId, Double refundAmount, String reason) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if (!"SUCCESS".equals(payment.getStatus())) {
                throw new RuntimeException("Cannot refund a failed payment");
            }

            if (refundAmount > payment.getAmount()) {
                throw new RuntimeException("Refund amount cannot exceed original payment amount");
            }

            // Mock refund processing
            payment.setStatus("REFUNDED");
            payment.setRefundAmount(refundAmount);
            payment.setRefundDate(LocalDateTime.now());
            payment.setFailureReason(reason);

            // Update booking status
            if (payment.getBooking() != null) {
                payment.getBooking().setStatus("REFUNDED");
                bookingRepository.save(payment.getBooking());
            }

            return paymentRepository.save(payment);
        } catch (Exception e) {
            System.err.println("Error processing refund: " + e.getMessage());
            throw new RuntimeException("Refund processing failed: " + e.getMessage());
        }
    }

    public List<Payment> getPaymentLogsByUser(String email) {
        try {
            return paymentRepository.findByBooking_User_Email(email);
        } catch (Exception e) {
            System.err.println("Error fetching user payment logs: " + e.getMessage());
            return List.of();
        }
    }

    public List<Payment> getPaymentLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return paymentRepository.findByPaymentDateBetween(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error fetching payment logs by date range: " + e.getMessage());
            return List.of();
        }
    }

    public Map<String, Object> getPaymentStatistics() {
        try {
            List<Payment> allPayments = paymentRepository.findAll();
            
            long totalPayments = allPayments.size();
            long successfulPayments = allPayments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .count();
            long failedPayments = allPayments.stream()
                    .filter(p -> "FAILED".equals(p.getStatus()))
                    .count();
            
            double totalRevenue = allPayments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            
            double totalRefunded = allPayments.stream()
                    .filter(p -> "REFUNDED".equals(p.getStatus()))
                    .mapToDouble(Payment::getRefundAmount)
                    .sum();

            return Map.of(
                "totalPayments", totalPayments,
                "successfulPayments", successfulPayments,
                "failedPayments", failedPayments,
                "successRate", totalPayments > 0 ? (double) successfulPayments / totalPayments * 100 : 0,
                "totalRevenue", totalRevenue,
                "totalRefunded", totalRefunded,
                "netRevenue", totalRevenue - totalRefunded
            );
        } catch (Exception e) {
            System.err.println("Error calculating payment statistics: " + e.getMessage());
            return Map.of();
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAdminPaymentDashboard() {
        try {
            // Use @Transactional to ensure Hibernate session is available
            List<Payment> allPayments = paymentRepository.findAll();
            List<Booking> allBookings = bookingRepository.findAll();
            List<DiscountCode> allDiscountCodes = discountCodeRepository.findAll();
            
            // Payment Statistics
            long totalPayments = allPayments.size();
            long successfulPayments = allPayments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .count();
            long failedPayments = allPayments.stream()
                    .filter(p -> "FAILED".equals(p.getStatus()))
                    .count();
            long pendingPayments = allPayments.stream()
                    .filter(p -> "PENDING".equals(p.getStatus()))
                    .count();
            
            double totalRevenue = allPayments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            
            double totalRefunded = allPayments.stream()
                    .filter(p -> "REFUNDED".equals(p.getStatus()))
                    .mapToDouble(p -> p.getRefundAmount() != null ? p.getRefundAmount() : 0.0)
                    .sum();
            
            // Booking Statistics
            long totalBookings = allBookings.size();
            long confirmedBookings = allBookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getStatus()))
                    .count();
            long cancelledBookings = allBookings.stream()
                    .filter(b -> "CANCELLED".equals(b.getStatus()))
                    .count();
            
            // Discount Code Statistics
            long totalDiscountCodes = allDiscountCodes.size();
            long activeDiscountCodes = allDiscountCodes.stream()
                    .filter(DiscountCode::getIsActive)
                    .count();
            long usedDiscountCodes = allDiscountCodes.stream()
                    .filter(dc -> dc.getUsedCount() > 0)
                    .count();
            
            // Recent Payments (last 10)
            List<Map<String, Object>> recentPayments = allPayments.stream()
                    .sorted((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()))
                    .limit(10)
                    .map(this::convertPaymentToMap)
                    .collect(Collectors.toList());
            
            // Payment Method Distribution
            Map<String, Long> paymentMethodDistribution = allPayments.stream()
                    .collect(Collectors.groupingBy(
                            Payment::getPaymentMethod,
                            Collectors.counting()
                    ));
            
            // Daily Revenue (last 7 days)
            Map<String, Double> dailyRevenue = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            for (int i = 6; i >= 0; i--) {
                LocalDateTime date = now.minusDays(i);
                String dateKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                double revenue = allPayments.stream()
                        .filter(p -> p.getPaymentDate().toLocalDate().equals(date.toLocalDate()))
                        .filter(p -> "SUCCESS".equals(p.getStatus()))
                        .mapToDouble(Payment::getAmount)
                        .sum();
                dailyRevenue.put(dateKey, revenue);
            }
            
            // Monthly Payment Trends (last 6 months)
            Map<String, Object> monthlyTrends = new HashMap<>();
            for (int i = 5; i >= 0; i--) {
                LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = monthStart.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);
                
                String monthKey = monthStart.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                
                long successfulInMonth = allPayments.stream()
                        .filter(p -> p.getPaymentDate().isAfter(monthStart) && p.getPaymentDate().isBefore(monthEnd))
                        .filter(p -> "SUCCESS".equals(p.getStatus()))
                        .count();
                        
                long failedInMonth = allPayments.stream()
                        .filter(p -> p.getPaymentDate().isAfter(monthStart) && p.getPaymentDate().isBefore(monthEnd))
                        .filter(p -> "FAILED".equals(p.getStatus()))
                        .count();
                
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("successful", successfulInMonth);
                monthData.put("failed", failedInMonth);
                monthlyTrends.put(monthKey, monthData);
            }
            
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("paymentStats", Map.of(
                "totalPayments", totalPayments,
                "successfulPayments", successfulPayments,
                "failedPayments", failedPayments,
                "pendingPayments", pendingPayments,
                "totalRevenue", totalRevenue,
                "totalRefunded", totalRefunded,
                "netRevenue", totalRevenue - totalRefunded,
                "successRate", totalPayments > 0 ? (double) successfulPayments / totalPayments * 100 : 0
            ));
            
            dashboard.put("bookingStats", Map.of(
                "totalBookings", totalBookings,
                "confirmedBookings", confirmedBookings,
                "cancelledBookings", cancelledBookings,
                "confirmationRate", totalBookings > 0 ? (double) confirmedBookings / totalBookings * 100 : 0
            ));
            
            dashboard.put("discountStats", Map.of(
                "totalDiscountCodes", totalDiscountCodes,
                "activeDiscountCodes", activeDiscountCodes,
                "usedDiscountCodes", usedDiscountCodes,
                "usageRate", totalDiscountCodes > 0 ? (double) usedDiscountCodes / totalDiscountCodes * 100 : 0
            ));
            
            dashboard.put("recentPayments", recentPayments);
            dashboard.put("paymentMethodDistribution", paymentMethodDistribution);
            dashboard.put("dailyRevenue", dailyRevenue);
            dashboard.put("monthlyTrends", monthlyTrends);
            
            return dashboard;
        } catch (Exception e) {
            System.err.println("Error generating admin payment dashboard: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentAnalytics(String startDate, String endDate, String status) {
        try {
            List<Payment> payments = paymentRepository.findAll();
            
            // Apply filters
            if (startDate != null && endDate != null) {
                LocalDateTime start = LocalDateTime.parse(startDate);
                LocalDateTime end = LocalDateTime.parse(endDate);
                payments = payments.stream()
                        .filter(p -> p.getPaymentDate().isAfter(start) && p.getPaymentDate().isBefore(end))
                        .collect(Collectors.toList());
            }
            
            if (status != null && !status.isEmpty()) {
                payments = payments.stream()
                        .filter(p -> status.equals(p.getStatus()))
                        .collect(Collectors.toList());
            }
            
            // Analytics calculations
            Map<String, Object> analytics = new HashMap<>();
            
            // Basic stats
            long totalPayments = payments.size();
            long successfulPayments = payments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .count();
            long failedPayments = payments.stream()
                    .filter(p -> "FAILED".equals(p.getStatus()))
                    .count();
            
            double totalRevenue = payments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            
            double averagePaymentAmount = payments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .average()
                    .orElse(0.0);
            
            // Payment method analysis
            Map<String, Long> methodCounts = payments.stream()
                    .collect(Collectors.groupingBy(
                            Payment::getPaymentMethod,
                            Collectors.counting()
                    ));
            
            Map<String, Double> methodRevenue = payments.stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()))
                    .collect(Collectors.groupingBy(
                            Payment::getPaymentMethod,
                            Collectors.summingDouble(Payment::getAmount)
                    ));
            
            // Status distribution
            Map<String, Long> statusDistribution = payments.stream()
                    .collect(Collectors.groupingBy(
                            Payment::getStatus,
                            Collectors.counting()
                    ));
            
            analytics.put("totalPayments", totalPayments);
            analytics.put("successfulPayments", successfulPayments);
            analytics.put("failedPayments", failedPayments);
            analytics.put("totalRevenue", totalRevenue);
            analytics.put("averagePaymentAmount", averagePaymentAmount);
            analytics.put("methodCounts", methodCounts);
            analytics.put("methodRevenue", methodRevenue);
            analytics.put("statusDistribution", statusDistribution);
            analytics.put("successRate", totalPayments > 0 ? (double) successfulPayments / totalPayments * 100 : 0);
            
            return analytics;
        } catch (Exception e) {
            System.err.println("Error generating payment analytics: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    private Map<String, Object> convertPaymentToMap(Payment payment) {
        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("paymentId", payment.getPaymentId());
        paymentMap.put("transactionId", payment.getTransactionId());
        paymentMap.put("amount", payment.getAmount());
        paymentMap.put("status", payment.getStatus());
        paymentMap.put("paymentMethod", payment.getPaymentMethod());
        paymentMap.put("paymentDate", payment.getPaymentDate().toString());
        paymentMap.put("gatewayResponse", payment.getGatewayResponse());
        paymentMap.put("failureReason", payment.getFailureReason());
        
        try {
            if (payment.getBooking() != null) {
                Booking booking = payment.getBooking();
                paymentMap.put("bookingId", booking.getBookingId());
                paymentMap.put("totalSeats", booking.getTotalSeats());
                paymentMap.put("totalAmount", booking.getTotalAmount());
                
                if (booking.getUser() != null) {
                    paymentMap.put("userEmail", booking.getUser().getEmail());
                    paymentMap.put("userName", booking.getUser().getUserName());
                }
                
                if (booking.getShowtime() != null) {
                    paymentMap.put("showtimeId", booking.getShowtime().getShowtimeId());
                    if (booking.getShowtime().getMovie() != null) {
                        paymentMap.put("movieTitle", booking.getShowtime().getMovie().getTitle());
                    }
                }
            }
        } catch (Exception e) {
            // If lazy loading fails, provide default values
            System.err.println("Warning: Could not load related entities for payment " + payment.getPaymentId() + ": " + e.getMessage());
            paymentMap.put("bookingId", "N/A");
            paymentMap.put("totalSeats", 0);
            paymentMap.put("totalAmount", 0.0);
            paymentMap.put("userEmail", "N/A");
            paymentMap.put("userName", "Unknown User");
            paymentMap.put("showtimeId", "N/A");
            paymentMap.put("movieTitle", "Unknown Movie");
        }
        
        return paymentMap;
    }
}
