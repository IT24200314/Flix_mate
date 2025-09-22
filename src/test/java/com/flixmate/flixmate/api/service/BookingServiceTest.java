package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_Success() {
        // Arrange
        String email = "user@example.com";
        Integer showtimeId = 1;
        Set<Integer> seatIds = Set.of(1, 2);
        
        User user = new User();
        user.setEmail(email);
        user.setUserName("Test User");
        
        UserStatus status = new UserStatus("ACTIVE");
        user.setStatus(status);

        CinemaHall hall = new CinemaHall();
        hall.setHallId(1);
        hall.setHallName("Hall A");

        Movie movie = new Movie();
        movie.setTitle("Test Movie");

        ShowTime showtime = new ShowTime();
        showtime.setShowtimeId(showtimeId);
        showtime.setPrice(10.0);
        showtime.setCinemaHall(hall);
        showtime.setMovie(movie);
        showtime.setStartTime(LocalDateTime.now().plusHours(1));

        Seat seat1 = new Seat();
        seat1.setSeatId(1);
        seat1.setStatus("AVAILABLE");
        seat1.setCinemaHall(hall);

        Seat seat2 = new Seat();
        seat2.setSeatId(2);
        seat2.setStatus("AVAILABLE");
        seat2.setCinemaHall(hall);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(showTimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatRepository.findById(1)).thenReturn(Optional.of(seat1));
        when(seatRepository.findById(2)).thenReturn(Optional.of(seat2));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setBookingId(1);
            return booking;
        });

        // Act
        Booking result = bookingService.createBooking(email, showtimeId, seatIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalSeats());
        assertEquals(20.0, result.getTotalAmount());
        assertEquals("PENDING", result.getStatus());
        assertEquals(user, result.getUser());
        assertEquals(showtime, result.getShowtime());
        assertEquals(2, result.getSeats().size());

        // Verify interactions
        verify(seatRepository, times(2)).save(any(Seat.class));
        verify(emailService).sendBookingConfirmation(eq(email), anyString(), eq("Test Movie"), anyString());
    }

    @Test
    void createBooking_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        Integer showtimeId = 1;
        Set<Integer> seatIds = Set.of(1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(email, showtimeId, seatIds));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createBooking_ShowtimeNotFound() {
        // Arrange
        String email = "user@example.com";
        Integer showtimeId = 999;
        Set<Integer> seatIds = Set.of(1);

        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(showTimeRepository.findById(showtimeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(email, showtimeId, seatIds));
        assertEquals("Showtime not found", exception.getMessage());
    }

    @Test
    void createBooking_SeatNotAvailable() {
        // Arrange
        String email = "user@example.com";
        Integer showtimeId = 1;
        Set<Integer> seatIds = Set.of(1);

        User user = new User();
        ShowTime showtime = new ShowTime();
        showtime.setPrice(10.0);
        showtime.setStartTime(LocalDateTime.now().plusHours(1));

        Seat seat = new Seat();
        seat.setSeatId(1);
        seat.setStatus("RESERVED"); // Not available

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(showTimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatRepository.findById(1)).thenReturn(Optional.of(seat));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(email, showtimeId, seatIds));
        assertEquals("Seat 1 is not available", exception.getMessage());
    }

    @Test
    void getAvailableSeats_Success() {
        // Arrange
        Integer showtimeId = 1;
        CinemaHall hall = new CinemaHall();
        hall.setHallId(1);

        ShowTime showtime = new ShowTime();
        showtime.setShowtimeId(showtimeId);
        showtime.setCinemaHall(hall);

        List<Seat> expectedSeats = List.of(new Seat(), new Seat());

        when(showTimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatRepository.findByCinemaHall_HallIdAndStatus(1, "AVAILABLE")).thenReturn(expectedSeats);

        // Act
        List<Seat> result = bookingService.getAvailableSeats(showtimeId);

        // Assert
        assertEquals(expectedSeats, result);
        verify(seatRepository).findByCinemaHall_HallIdAndStatus(1, "AVAILABLE");
    }

    @Test
    void getUserBookings_Success() {
        // Arrange
        String email = "user@example.com";
        List<Booking> expectedBookings = List.of(new Booking(), new Booking());

        when(bookingRepository.findByUserEmail(email)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getUserBookings(email);

        // Assert
        assertEquals(expectedBookings, result);
        verify(bookingRepository).findByUserEmail(email);
    }
}




