package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

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
                // Simulate 90% success rate for credit cards
                if (Math.random() < 0.9) {
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
                // Simulate 85% success rate for e-wallet
                if (Math.random() < 0.85) {
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

            if ("SUCCESS".equals(status)) {
                booking.setStatus("CONFIRMED");
                bookingRepository.save(booking);
            } else {
                booking.setStatus("PAYMENT_FAILED");
                bookingRepository.save(booking);
            }

            return paymentRepository.save(payment);
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
}
