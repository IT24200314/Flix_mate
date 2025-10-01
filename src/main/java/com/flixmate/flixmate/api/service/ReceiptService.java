package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    public Receipt generateReceipt(Integer bookingId, Integer paymentId) {
        try {
            System.out.println("=== RECEIPT SERVICE DEBUG ===");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Payment ID: " + paymentId);
            
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            System.out.println("Found booking: " + booking.getBookingId());
            
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            System.out.println("Found payment: " + payment.getPaymentId());

            // Generate unique receipt number
            String receiptNumber = generateReceiptNumber();
            
            // Format showtime date and time
            LocalDateTime startTime = booking.getShowtime().getStartTime();
            String showtimeDate = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String showtimeTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            
            // Format seat numbers
            String seatNumbers = booking.getSeats().stream()
                    .map(seat -> seat.getRow() + seat.getNumber())
                    .collect(Collectors.joining(", "));
            
            // Get cinema hall name
            String cinemaHall = "Hall " + booking.getShowtime().getCinemaHall().getHallId();
            
            Receipt receipt = new Receipt(
                    receiptNumber,
                    LocalDateTime.now(),
                    booking.getShowtime().getMovie().getTitle(),
                    showtimeDate,
                    showtimeTime,
                    cinemaHall,
                    seatNumbers,
                    booking.getTotalSeats(),
                    booking.getTotalAmount(),
                    payment.getPaymentMethod(),
                    payment.getTransactionId(),
                    booking.getUser().getUserName(),
                    booking.getUser().getEmail(),
                    booking,
                    payment
            );
            
            System.out.println("Creating receipt with number: " + receiptNumber);
            Receipt savedReceipt = receiptRepository.save(receipt);
            System.out.println("Receipt saved successfully with ID: " + savedReceipt.getReceiptId());
            return savedReceipt;
            
        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
            throw new RuntimeException("Failed to generate receipt", e);
        }
    }
    
    public List<Receipt> getUserReceipts(String userEmail) {
        return receiptRepository.findByUserEmailOrderByReceiptDateDesc(userEmail);
    }
    
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }
    
    public Receipt getReceiptByNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber);
    }
    
    public Receipt getReceiptByBookingId(Integer bookingId) {
        return receiptRepository.findByBookingId(bookingId);
    }
    
    private String generateReceiptNumber() {
        // Generate receipt number with timestamp and random suffix
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.valueOf((int)(Math.random() * 1000));
        return "RCP-" + timestamp + "-" + randomSuffix;
    }
}
