package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Mock payment gateway logic
        String transactionId = UUID.randomUUID().toString();
        LocalDateTime paymentDate = LocalDateTime.now();
        String status = "SUCCESS"; // Always succeed for demo purposes

        Payment payment = new Payment(transactionId, paymentDate, paymentMethod, amount, status, booking);
        if ("SUCCESS".equals(status)) {
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);
        }

        return paymentRepository.save(payment);
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
}
