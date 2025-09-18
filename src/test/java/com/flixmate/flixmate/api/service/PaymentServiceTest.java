package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processPayment_Success() {
        // Arrange
        Integer bookingId = 1;
        String paymentMethod = "CREDIT_CARD";
        
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setTotalAmount(50.0);
        booking.setStatus("PENDING");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Payment result = paymentService.processPayment(bookingId, paymentMethod);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTransactionId());
        assertEquals(paymentMethod, result.getPaymentMethod());
        assertEquals(50.0, result.getAmount());
        assertNotNull(result.getPaymentDate());
        assertTrue(result.getStatus().equals("SUCCESS") || result.getStatus().equals("FAILED"));
        
        if ("SUCCESS".equals(result.getStatus())) {
            assertEquals("PAID", booking.getStatus());
            verify(bookingRepository).save(booking);
        }

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_BookingNotFound() {
        // Arrange
        Integer bookingId = 999;
        String paymentMethod = "CREDIT_CARD";

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> paymentService.processPayment(bookingId, paymentMethod));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getPaymentLogs_Success() {
        // Arrange
        List<Payment> expectedPayments = List.of(new Payment(), new Payment());
        when(paymentRepository.findAll()).thenReturn(expectedPayments);

        // Act
        List<Payment> result = paymentService.getPaymentLogs();

        // Assert
        assertEquals(expectedPayments, result);
        verify(paymentRepository).findAll();
    }

    @Test
    void processPayment_MultipleAttempts() {
        // Arrange
        Integer bookingId = 1;
        String paymentMethod = "DEBIT_CARD";
        
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setTotalAmount(25.0);
        booking.setStatus("PENDING");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Process multiple payments
        Payment result1 = paymentService.processPayment(bookingId, paymentMethod);
        Payment result2 = paymentService.processPayment(bookingId, paymentMethod);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.getTransactionId(), result2.getTransactionId());
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }
}
