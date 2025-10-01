package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    @Autowired
    private SeatRepository seatRepository;

    private User testUser;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Clean up test data
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        showTimeRepository.deleteAll();
        cinemaHallRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        // Create test data
        createTestData();
    }

    private void createTestData() {
        // Create test user
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        // testUser.setRole("USER"); // User entity uses status instead of role
        testUser = userRepository.save(testUser);

        // Create test movie
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setLanguage("English");
        movie.setDirector("Test Director");
        movie.setIsActive(true);
        movie = movieRepository.save(movie);

        // Create test cinema hall
        CinemaHall hall = new CinemaHall();
        hall.setHallName("Test Hall");
        hall.setCapacity(100);
        hall = cinemaHallRepository.save(hall);

        // Create test showtime
        ShowTime showtime = new ShowTime();
        showtime.setMovie(movie);
        showtime.setCinemaHall(hall);
        showtime.setStartTime(LocalDateTime.now().plusHours(2));
        showtime.setEndTime(LocalDateTime.now().plusHours(4));
        showtime.setPrice(15.0);
        showtime = showTimeRepository.save(showtime);

        // Create test seat
        Seat seat = new Seat();
        seat.setCinemaHall(hall);
        seat.setRow("A");
        seat.setNumber(1);
        seat.setStatus("AVAILABLE");
        seat = seatRepository.save(seat);

        // Create test booking
        testBooking = new Booking();
        testBooking.setUser(testUser);
        testBooking.setShowtime(showtime);
        testBooking.setBookingDate(LocalDateTime.now());
        testBooking.setTotalSeats(1);
        testBooking.setTotalAmount(15.0);
        testBooking.setStatus("PENDING");
        Set<Seat> seats = new HashSet<>();
        seats.add(seat);
        testBooking.setSeats(seats);
        testBooking = bookingRepository.save(testBooking);
    }

    @Test
    void testProcessPaymentSuccess() {
        // Test successful payment
        Payment payment = paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        assertNotNull(payment);
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
        assertEquals(15.0, payment.getAmount());
        assertEquals("SUCCESS", payment.getStatus());
        assertNotNull(payment.getTransactionId());
        assertNotNull(payment.getPaymentDate());

        // Verify booking status was updated
        Booking updatedBooking = bookingRepository.findById(testBooking.getBookingId()).orElse(null);
        assertNotNull(updatedBooking);
        assertEquals("CONFIRMED", updatedBooking.getStatus());
    }

    @Test
    void testProcessPaymentFailed() {
        // Mock a failed payment by using an invalid payment method
        assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(testBooking.getBookingId(), "INVALID_METHOD", 15.0);
        });
    }

    @Test
    void testProcessPaymentInvalidAmount() {
        // Test with invalid amount
        assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", -10.0);
        });
    }

    @Test
    void testProcessPaymentBookingNotFound() {
        // Test with non-existent booking
        assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(999, "CREDIT_CARD", 15.0);
        });
    }

    @Test
    void testGetPaymentById() {
        // Create a payment first
        Payment payment = paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test getting payment by ID
        Payment retrievedPayment = paymentService.getPaymentById(payment.getPaymentId());
        assertNotNull(retrievedPayment);
        assertEquals(payment.getPaymentId(), retrievedPayment.getPaymentId());
        assertEquals(payment.getTransactionId(), retrievedPayment.getTransactionId());
    }

    @Test
    void testGetPaymentByIdNotFound() {
        Payment payment = paymentService.getPaymentById(999);
        assertNull(payment);
    }

    @Test
    void testUpdatePaymentStatus() {
        // Create a payment first
        Payment payment = paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test updating payment status
        boolean updated = paymentService.updatePaymentStatus(payment.getPaymentId(), "REFUNDED");
        assertTrue(updated);

        // Verify status was updated
        Payment updatedPayment = paymentService.getPaymentById(payment.getPaymentId());
        assertEquals("REFUNDED", updatedPayment.getStatus());
    }

    @Test
    void testUpdatePaymentStatusNotFound() {
        boolean updated = paymentService.updatePaymentStatus(999, "REFUNDED");
        assertFalse(updated);
    }

    @Test
    void testDeletePayment() {
        // Create a payment first
        Payment payment = paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test deleting payment
        boolean deleted = paymentService.deletePayment(payment.getPaymentId());
        assertTrue(deleted);

        // Verify payment was deleted
        Payment deletedPayment = paymentService.getPaymentById(payment.getPaymentId());
        assertNull(deletedPayment);
    }

    @Test
    void testDeletePaymentNotFound() {
        boolean deleted = paymentService.deletePayment(999);
        assertFalse(deleted);
    }

    @Test
    void testGetPaymentLogs() {
        // Create multiple payments
        paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test getting payment logs
        List<Payment> logs = paymentService.getPaymentLogs();
        assertNotNull(logs);
        assertFalse(logs.isEmpty());
        assertEquals(1, logs.size());
    }

    @Test
    void testProcessRefund() {
        // Create a successful payment first
        Payment payment = paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test processing refund
        Payment refundedPayment = paymentService.processRefund(payment.getPaymentId(), 10.0, "Customer request");

        assertNotNull(refundedPayment);
        assertEquals("REFUNDED", refundedPayment.getStatus());
        assertEquals(10.0, refundedPayment.getRefundAmount());
        assertEquals("Customer request", refundedPayment.getFailureReason());
        assertNotNull(refundedPayment.getRefundDate());

        // Verify booking status was updated
        Booking updatedBooking = bookingRepository.findById(testBooking.getBookingId()).orElse(null);
        assertNotNull(updatedBooking);
        assertEquals("REFUNDED", updatedBooking.getStatus());
    }

    @Test
    void testProcessRefundInvalidAmount() {
        // Create a successful payment first
        Payment payment = paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test refund with amount exceeding original payment
        assertThrows(RuntimeException.class, () -> {
            paymentService.processRefund(payment.getPaymentId(), 20.0, "Customer request");
        });
    }

    @Test
    void testProcessRefundFailedPayment() {
        // Create a payment with failed status (simulate by creating directly)
        Payment failedPayment = new Payment();
        failedPayment.setTransactionId("FAILED_TXN");
        failedPayment.setPaymentDate(LocalDateTime.now());
        failedPayment.setPaymentMethod("CREDIT_CARD");
        failedPayment.setAmount(15.0);
        failedPayment.setStatus("FAILED");
        failedPayment.setBooking(testBooking);
        failedPayment = paymentRepository.save(failedPayment);

        // Test refund on failed payment
        final Payment finalFailedPayment = failedPayment;
        assertThrows(RuntimeException.class, () -> {
            paymentService.processRefund(finalFailedPayment.getPaymentId(), 15.0, "Customer request");
        });
    }

    @Test
    void testGetPaymentStatistics() {
        // Create multiple payments with different statuses
        paymentService.processPayment(testBooking.getBookingId(), "CREDIT_CARD", 15.0);

        // Test getting payment statistics
        Map<String, Object> statistics = paymentService.getPaymentStatistics();
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("totalPayments"));
        assertTrue(statistics.containsKey("successfulPayments"));
        assertTrue(statistics.containsKey("failedPayments"));
        assertTrue(statistics.containsKey("totalRevenue"));
        assertTrue(statistics.containsKey("netRevenue"));

        assertEquals(1, statistics.get("totalPayments"));
        assertEquals(1, statistics.get("successfulPayments"));
        assertEquals(0, statistics.get("failedPayments"));
        assertEquals(15.0, statistics.get("totalRevenue"));
    }
}