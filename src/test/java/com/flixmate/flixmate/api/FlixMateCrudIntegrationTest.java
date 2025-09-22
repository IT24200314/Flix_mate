package com.flixmate.flixmate.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive CRUD Integration Test for FlixMate API
 * Tests all 6 functions with complete CRUD operations
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FlixMateCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Repositories
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ShowTimeRepository showTimeRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReportRepository reportRepository;

    // Test data
    private User testUser;
    private UserStatus userStatus;
    private Movie testMovie;
    private CinemaHall testHall;
    private Seat testSeat1, testSeat2;
    private ShowTime testShowtime;
    private Booking testBooking;
    private Payment testPayment;
    private Review testReview;
    private Report testReport;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
        reviewRepository.deleteAll();
        reportRepository.deleteAll();
        seatRepository.deleteAll();
        showTimeRepository.deleteAll();
        cinemaHallRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
        userStatusRepository.deleteAll();

        // Create test data
        setupTestData();
    }

    private void setupTestData() {
        // Create user status
        userStatus = new UserStatus("STANDARD", "USER");
        userStatus = userStatusRepository.save(userStatus);

        // Create test user
        testUser = new User();
        testUser.setUserName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setPhone("1234567890");
        testUser.setStatus(userStatus);
        testUser = userRepository.save(testUser);

        // Create test movie
        testMovie = new Movie();
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("A test movie for CRUD testing");
        testMovie.setGenre("Action");
        testMovie.setReleaseYear(2025);
        testMovie.setDuration(120);
        testMovie.setDirector("Test Director");
        testMovie.setCast("Test Actor 1, Test Actor 2");
        testMovie = movieRepository.save(testMovie);

        // Create test cinema hall
        testHall = new CinemaHall();
        testHall.setHallName("Test Hall");
        testHall.setCapacity(100);
        testHall.setLocation("Test Location");
        testHall = cinemaHallRepository.save(testHall);

        // Create test seats
        testSeat1 = new Seat();
        testSeat1.setRow("A");
        testSeat1.setNumber(1);
        // testSeat1.setSeatType("Standard"); // Seat entity doesn't have seatType field
        testSeat1.setStatus("AVAILABLE");
        testSeat1.setCinemaHall(testHall);
        testSeat1 = seatRepository.save(testSeat1);

        testSeat2 = new Seat();
        testSeat2.setRow("A");
        testSeat2.setNumber(2);
        // testSeat2.setSeatType("Standard"); // Seat entity doesn't have seatType field
        testSeat2.setStatus("AVAILABLE");
        testSeat2.setCinemaHall(testHall);
        testSeat2 = seatRepository.save(testSeat2);

        // Create test showtime
        testShowtime = new ShowTime();
        testShowtime.setMovie(testMovie);
        testShowtime.setCinemaHall(testHall);
        testShowtime.setStartTime(LocalDateTime.now().plusHours(1));
        testShowtime.setEndTime(LocalDateTime.now().plusHours(3));
        testShowtime.setPrice(15.0);
        testShowtime = showTimeRepository.save(testShowtime);

        // Create test booking
        testBooking = new Booking();
        testBooking.setUser(testUser);
        testBooking.setShowtime(testShowtime);
        testBooking.setBookingDate(LocalDateTime.now());
        testBooking.setTotalSeats(2);
        testBooking.setTotalAmount(30.0);
        testBooking.setStatus("PENDING");
        testBooking.setSeats(Set.of(testSeat1, testSeat2));
        testBooking = bookingRepository.save(testBooking);

        // Create test payment
        testPayment = new Payment();
        testPayment.setBooking(testBooking);
        testPayment.setAmount(30.0);
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setPaymentMethod("CARD");
        testPayment.setTransactionId("TEST123");
        testPayment.setStatus("SUCCESS");
        testPayment = paymentRepository.save(testPayment);

        // Create test review
        testReview = new Review();
        testReview.setUser(testUser);
        testReview.setMovie(testMovie);
        testReview.setRating(5);
        testReview.setComment("Great movie!");
        testReview.setReviewDate(LocalDateTime.now());
        testReview = reviewRepository.save(testReview);

        // Create test report
        testReport = new Report();
        testReport.setUser(testUser);
        testReport.setType("REVENUE");
        testReport.setData("{\"totalRevenue\": 1000.0}");
        testReport.setGeneratedDate(LocalDateTime.now());
        testReport = reportRepository.save(testReport);
    }

    // ==================== FUNCTION 1: MOVIE LISTINGS AND SHOWTIMES ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testMovieCrud_Create() throws Exception {
        // Test CREATE - Add new movie
        Movie newMovie = new Movie();
        newMovie.setTitle("New Test Movie");
        newMovie.setDescription("A new test movie");
        newMovie.setGenre("Drama");
        newMovie.setReleaseYear(2025);
        newMovie.setDuration(150);
        newMovie.setDirector("New Director");
        newMovie.setCast("New Actor 1, New Actor 2");

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie added successfully"));
    }

    @Test
    void testMovieCrud_Read() throws Exception {
        // Test READ - Get all movies
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Movie"));

        // Test READ - Get movie by ID
        mockMvc.perform(get("/api/movies/{id}", testMovie.getMovieId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.genre").value("Action"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testMovieCrud_Update() throws Exception {
        // Test UPDATE - Update movie
        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Updated Test Movie");
        updatedMovie.setDescription("Updated description");
        updatedMovie.setGenre("Comedy");
        updatedMovie.setReleaseYear(2025);
        updatedMovie.setDuration(130);
        updatedMovie.setDirector("Updated Director");
        updatedMovie.setCast("Updated Actor 1, Updated Actor 2");

        mockMvc.perform(put("/api/movies/{id}", testMovie.getMovieId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testMovieCrud_Delete() throws Exception {
        // Test DELETE - Delete movie
        mockMvc.perform(delete("/api/movies/{id}", testMovie.getMovieId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted successfully"));
    }

    // ==================== FUNCTION 2: PERFORMANCE, REVENUE, AND TRENDS (REPORTS) ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReportCrud_Create() throws Exception {
        // Test CREATE - Generate revenue report
        mockMvc.perform(post("/api/reports")
                .param("type", "REVENUE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("REVENUE"));

        // Test CREATE - Generate popularity report
        mockMvc.perform(post("/api/reports")
                .param("type", "POPULARITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("POPULARITY"));

        // Test CREATE - Generate ticket sales report
        mockMvc.perform(post("/api/reports")
                .param("type", "TICKET_SALES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TICKET_SALES"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReportCrud_Read() throws Exception {
        // Test READ - Get all reports
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("REVENUE"));

        // Test READ - Get specific report types
        mockMvc.perform(get("/api/reports/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("REVENUE"));

        mockMvc.perform(get("/api/reports/popularity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("POPULARITY"));

        mockMvc.perform(get("/api/reports/ticket-sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TICKET_SALES"));

        // Test READ - Get report by ID
        mockMvc.perform(get("/api/reports/{id}", testReport.getReportId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("REVENUE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReportCrud_Delete() throws Exception {
        // Test DELETE - Delete report
        mockMvc.perform(delete("/api/reports/{id}", testReport.getReportId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Report deleted successfully"));
    }

    // ==================== FUNCTION 3: SECURE PAYMENT PROCESSING ====================

    @Test
    void testPaymentCrud_Create() throws Exception {
        // Test CREATE - Process payment
        // Create a simple payment request object
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("bookingId", testBooking.getBookingId());
        paymentRequest.put("paymentMethod", "CARD");
        paymentRequest.put("amount", 30.0);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment processed"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPaymentCrud_Read() throws Exception {
        // Test READ - Get payment logs
        mockMvc.perform(get("/api/payments/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].amount").value(30.0));

        // Test READ - Get payment by ID
        mockMvc.perform(get("/api/payments/{id}", testPayment.getPaymentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(30.0))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPaymentCrud_Update() throws Exception {
        // Test UPDATE - Update payment status
        mockMvc.perform(put("/api/payments/{id}", testPayment.getPaymentId())
                .param("status", "REFUNDED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment status updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPaymentCrud_Delete() throws Exception {
        // Test DELETE - Delete payment
        mockMvc.perform(delete("/api/payments/{id}", testPayment.getPaymentId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment deleted successfully"));
    }

    // ==================== FUNCTION 4: BOOK TICKETS (SEAT SELECTION & CHECKOUT) ====================

    @Test
    @WithMockUser(username = "test@example.com")
    void testBookingCrud_Create() throws Exception {
        // Test CREATE - Create booking
        Set<Integer> seatIds = Set.of(testSeat1.getSeatId(), testSeat2.getSeatId());
        String requestBody = objectMapper.writeValueAsString(seatIds);

        mockMvc.perform(post("/api/bookings/{showtimeId}", testShowtime.getShowtimeId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSeats").value(2))
                .andExpect(jsonPath("$.totalAmount").value(30.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testBookingCrud_Read() throws Exception {
        // Test READ - Get user bookings
        mockMvc.perform(get("/api/bookings/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalSeats").value(2));

        // Test READ - Get available seats
        mockMvc.perform(get("/api/bookings/available/{showtimeId}", testShowtime.getShowtimeId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // Test READ - Get booking by ID
        mockMvc.perform(get("/api/bookings/{id}", testBooking.getBookingId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSeats").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testBookingCrud_Update() throws Exception {
        // Test UPDATE - Update booking
        Set<Integer> newSeatIds = Set.of(testSeat1.getSeatId());
        String requestBody = objectMapper.writeValueAsString(newSeatIds);

        mockMvc.perform(put("/api/bookings/{id}", testBooking.getBookingId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking updated successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testBookingCrud_Delete() throws Exception {
        // Test DELETE - Delete booking
        mockMvc.perform(delete("/api/bookings/{id}", testBooking.getBookingId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking deleted successfully"));
    }

    // ==================== FUNCTION 5: BROWSE MOVIES, SHOWTIMES, AND LOCATIONS ====================

    @Test
    void testBrowseCrud_Read() throws Exception {
        // Test READ - Browse movies with filters
        mockMvc.perform(get("/api/movies")
                .param("title", "Test")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Movie"));

        // Test READ - Browse movies by genre
        mockMvc.perform(get("/api/movies")
                .param("genre", "Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].genre").value("Action"));
    }

    // ==================== FUNCTION 6: RATE AND REVIEW MOVIES ====================

    @Test
    @WithMockUser(username = "test@example.com")
    void testReviewCrud_Create() throws Exception {
        // Test CREATE - Add review
        mockMvc.perform(post("/api/movies/{movieId}/reviews", testMovie.getMovieId())
                .param("rating", "4")
                .param("comment", "Good movie!"))
                .andExpect(status().isOk())
                .andExpect(content().string("Review added successfully"));
    }

    @Test
    void testReviewCrud_Read() throws Exception {
        // Test READ - Get movie reviews
        mockMvc.perform(get("/api/movies/{movieId}/reviews", testMovie.getMovieId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Great movie!"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testReviewCrud_Update() throws Exception {
        // Test UPDATE - Update review
        mockMvc.perform(put("/api/movies/{movieId}/reviews/{reviewId}", 
                testMovie.getMovieId(), testReview.getReviewId())
                .param("rating", "4")
                .param("comment", "Updated review"))
                .andExpect(status().isOk())
                .andExpect(content().string("Review updated successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testReviewCrud_Delete() throws Exception {
        // Test DELETE - Delete review
        mockMvc.perform(delete("/api/movies/{movieId}/reviews/{reviewId}", 
                testMovie.getMovieId(), testReview.getReviewId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted successfully"));
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    void testErrorHandling_NotFound() throws Exception {
        // Test 404 errors
        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testErrorHandling_Unauthorized() throws Exception {
        // Test 401 errors for protected endpoints
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Movie())))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/payments/logs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testErrorHandling_InvalidData() throws Exception {
        // Test invalid data handling
        mockMvc.perform(post("/api/movies/{movieId}/reviews", testMovie.getMovieId())
                .param("rating", "6")  // Invalid rating > 5
                .param("comment", "Invalid rating"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/movies/{movieId}/reviews", testMovie.getMovieId())
                .param("rating", "0")  // Invalid rating < 1
                .param("comment", "Invalid rating"))
                .andExpect(status().isBadRequest());
    }
}
