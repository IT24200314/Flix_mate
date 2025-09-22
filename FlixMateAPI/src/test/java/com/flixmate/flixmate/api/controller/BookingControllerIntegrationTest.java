package com.flixmate.flixmate.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class BookingControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private ShowTime testShowtime;
    private Seat testSeat;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        
        // Clean up test data
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
        testShowtime = new ShowTime();
        testShowtime.setMovie(movie);
        testShowtime.setCinemaHall(hall);
        testShowtime.setStartTime(LocalDateTime.now().plusHours(2));
        testShowtime.setEndTime(LocalDateTime.now().plusHours(4));
        testShowtime.setPrice(15.0);
        testShowtime = showTimeRepository.save(testShowtime);

        // Create test seat
        testSeat = new Seat();
        testSeat.setCinemaHall(hall);
        testSeat.setRow("A");
        testSeat.setNumber(1);
        testSeat.setStatus("AVAILABLE");
        testSeat = seatRepository.save(testSeat);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetUserBookings() throws Exception {
        // Create test booking
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setShowtime(testShowtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalSeats(1);
        booking.setTotalAmount(15.0);
        booking.setStatus("CONFIRMED");
        Set<Seat> seats = new HashSet<>();
        seats.add(testSeat);
        booking.setSeats(seats);
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalSeats").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(15.0))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetAvailableSeats() throws Exception {
        mockMvc.perform(get("/api/bookings/available/{showtimeId}", testShowtime.getShowtimeId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].seatId").value(testSeat.getSeatId()))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCreateBooking() throws Exception {
        Set<Integer> seatIds = new HashSet<>();
        seatIds.add(testSeat.getSeatId());

        mockMvc.perform(post("/api/bookings/{showtimeId}", testShowtime.getShowtimeId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seatIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalSeats").value(1))
                .andExpect(jsonPath("$.totalAmount").value(15.0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Verify booking was created
        assert bookingRepository.findAll().size() == 1;
        Booking booking = bookingRepository.findAll().get(0);
        assert booking.getUser().getEmail().equals("test@example.com");
        assert booking.getShowtime().getShowtimeId().equals(testShowtime.getShowtimeId());
        assert booking.getTotalSeats() == 1;
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCreateBookingWithInvalidSeat() throws Exception {
        Set<Integer> seatIds = new HashSet<>();
        seatIds.add(999); // Non-existent seat

        mockMvc.perform(post("/api/bookings/{showtimeId}", testShowtime.getShowtimeId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seatIds)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetBookingById() throws Exception {
        // Create test booking
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setShowtime(testShowtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalSeats(1);
        booking.setTotalAmount(15.0);
        booking.setStatus("CONFIRMED");
        Set<Seat> seats = new HashSet<>();
        seats.add(testSeat);
        booking.setSeats(seats);
        Booking savedBooking = bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/{id}", savedBooking.getBookingId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookingId").value(savedBooking.getBookingId()))
                .andExpect(jsonPath("$.totalSeats").value(1))
                .andExpect(jsonPath("$.totalAmount").value(15.0));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetBookingByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/bookings/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserBookingsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCreateBookingWithDiscount() throws Exception {
        Set<Integer> seatIds = new HashSet<>();
        seatIds.add(testSeat.getSeatId());

        // Create booking request with discount
        BookingController.BookingRequest request = new BookingController.BookingRequest();
        request.setSeatIds(seatIds);
        request.setDiscountCode("TEST10");
        request.setLoyaltyPointsToRedeem(100);

        mockMvc.perform(post("/api/bookings/{showtimeId}/with-discount", testShowtime.getShowtimeId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalSeats").value(1));
    }
}