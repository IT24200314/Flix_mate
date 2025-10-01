package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAll();
    List<Payment> findByBooking_User_Email(String email);
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByStatus(String status);
    
    @Modifying
    @Query(value = "DELETE FROM payments WHERE booking_id IN (SELECT booking_id FROM bookings WHERE showtime_id IN (SELECT showtime_id FROM showtimes WHERE movie_id = :movieId))", nativeQuery = true)
    int deletePaymentsForMovie(@Param("movieId") Integer movieId);
}
