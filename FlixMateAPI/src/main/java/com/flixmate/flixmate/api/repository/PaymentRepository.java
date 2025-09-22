package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAll();
    List<Payment> findByBooking_User_Email(String email);
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByStatus(String status);
}
