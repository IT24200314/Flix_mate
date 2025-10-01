package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    
    List<Receipt> findByUserEmailOrderByReceiptDateDesc(String userEmail);
    
    Receipt findByReceiptNumber(String receiptNumber);
    
    @Query("SELECT r FROM Receipt r WHERE r.booking.bookingId = :bookingId")
    Receipt findByBookingId(@Param("bookingId") Integer bookingId);
    
    @Query("SELECT r FROM Receipt r WHERE r.payment.paymentId = :paymentId")
    Receipt findByPaymentId(@Param("paymentId") Integer paymentId);
}
