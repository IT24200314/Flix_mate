package com.flixmate.flixmate.api.controller;

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
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User testUser;
    private ShowTime testShowtime;
    private Seat testSeat1;
    private Seat testSeat2;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        showTimeRepository.deleteAll();
        cinemaHallRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
        userStatusRepository.deleteAll();

        // Create test data
        UserStatus status = new UserStatus("ACTIVE");
        status = userStatusRepository.save(status);

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setUserName("Test User");
        testUser.setStatus(status);
        testUser = userRepository.save(testUser);

        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie = movieRepository.save(movie);

        CinemaHall hall = new CinemaHall();
        hall.setHallName("Test Hall");
        hall.setCapacity(100);
        hall = cinemaHallRepository.save(hall);

        testShowtime = new ShowTime();
        testShowtime.setStartTime(LocalDateTime.now().plusHours(1));
        testShowtime.setEndTime(LocalDateTime.now().plusHours(3));
        testShowtime.setPrice(10.0);
        testShowtime.setMovie(movie);
        testShowtime.setCinemaHall(hall);
        testShowtime = showTimeRepository.save(testShowtime);

        testSeat1 = new Seat();
        testSeat1.setRow("A");
        testSeat1.setNumber(1);
        testSeat1.setStatus("AVAILABLE");
        testSeat1.setCinemaHall(hall);
        testSeat1 = seatRepository.save(testSeat1);

        testSeat2 = new Seat();
        testSeat2.setRow("A");
        testSeat2.setNumber(2);
        testSeat2.setStatus("AVAILABLE");
        testSeat2.setCinemaHall(hall);
        testSeat2 = seatRepository.save(testSeat2);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createBooking_Success() throws Exception {
        String requestBody = objectMapper.writeValueAsString(Set.of(testSeat1.getSeatId(), testSeat2.getSeatId()));

        mockMvc.perform(post("/api/bookings/{showtimeId}", testShowtime.getShowtimeId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSeats").value(2))
                .andExpect(jsonPath("$.totalAmount").value(20.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createBooking_InvalidShowtime() throws Exception {
        String requestBody = objectMapper.writeValueAsString(Set.of(testSeat1.getSeatId()));

        mockMvc.perform(post("/api/bookings/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getUserBookings_Success() throws Exception {
        // Create a booking first
        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalSeats(2);
        booking.setTotalAmount(20.0);
        booking.setStatus("PENDING");
        booking.setUser(testUser);
        booking.setShowtime(testShowtime);
        booking.setSeats(Set.of(testSeat1, testSeat2));
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalSeats").value(2));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getAvailableSeats_Success() throws Exception {
        mockMvc.perform(get("/api/bookings/available/{showtimeId}", testShowtime.getShowtimeId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}


