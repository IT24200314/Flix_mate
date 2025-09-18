package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private LoyaltyPointsService loyaltyPointsService;
    
    @Autowired
    private DiscountCodeService discountCodeService;

    public List<Booking> getUserBookings(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            
            System.out.println("=== BOOKING SERVICE: getUserBookings ===");
            System.out.println("Email: " + email);
            
            List<Booking> bookings = bookingRepository.findByUserEmail(email);
            System.out.println("Found " + bookings.size() + " bookings for user: " + email);
            
            return bookings;
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: getUserBookings ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Email: " + email);
            e.printStackTrace();
            throw new RuntimeException("Failed to get user bookings: " + e.getMessage());
        }
    }

    public Booking createBooking(String email, Integer showtimeId, Set<Integer> seatIds) {
        System.out.println("=== BOOKING SERVICE: createBooking ===");
        System.out.println("Email: " + email);
        System.out.println("Showtime ID: " + showtimeId);
        System.out.println("Seat IDs: " + seatIds);
        
        try {
            // Input validation
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            if (showtimeId == null || showtimeId <= 0) {
                throw new IllegalArgumentException("Invalid showtime ID: " + showtimeId);
            }
            if (seatIds == null || seatIds.isEmpty()) {
                throw new IllegalArgumentException("Seat IDs cannot be null or empty");
            }
            if (seatIds.size() > 20) {
                throw new IllegalArgumentException("Cannot book more than 20 seats at once");
            }
            
            // Find user with better error handling
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            System.out.println("Found user: " + user.getEmail() + " (ID: " + user.getUserId() + ")");
            
            // Find showtime with better error handling
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + showtimeId));
            System.out.println("Found showtime: " + showtime.getShowtimeId() + 
                             " (Movie: " + (showtime.getMovie() != null ? showtime.getMovie().getTitle() : "NULL") + 
                             ", Hall: " + (showtime.getCinemaHall() != null ? showtime.getCinemaHall().getHallId() : "NULL") + ")");

            // Validate and collect seats
            Set<Seat> seats = new HashSet<>();
            for (Integer seatId : seatIds) {
                if (seatId == null || seatId <= 0) {
                    throw new IllegalArgumentException("Invalid seat ID: " + seatId);
                }
                
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Seat not found with ID: " + seatId));
                
                if (!"AVAILABLE".equals(seat.getStatus())) {
                    throw new RuntimeException("Seat " + seatId + " is not available. Current status: " + seat.getStatus());
                }
                
                // Check if seat belongs to the same hall as the showtime
                if (seat.getCinemaHall() == null || showtime.getCinemaHall() == null ||
                    !seat.getCinemaHall().getHallId().equals(showtime.getCinemaHall().getHallId())) {
                    throw new RuntimeException("Seat " + seatId + " does not belong to the same hall as the showtime");
                }
                
                seats.add(seat);
                System.out.println("Added seat: " + seatId + " (Row: " + seat.getRow() + ", Number: " + seat.getNumber() + ")");
            }

            // Calculate total amount
            if (showtime.getPrice() == null || showtime.getPrice() < 0) {
                throw new RuntimeException("Invalid showtime price: " + showtime.getPrice());
            }
            
            Double totalAmount = showtime.getPrice() * seatIds.size();
            System.out.println("Total amount: " + totalAmount);
            
            // Create booking object
            Booking booking = new Booking(
                    LocalDateTime.now(), seatIds.size(), totalAmount, "PENDING", user, showtime, seats
            );
            System.out.println("Created booking object");

            // Reserve seats
            for (Seat seat : seats) {
                seat.setStatus("RESERVED");
                seatRepository.save(seat);
                System.out.println("Reserved seat: " + seat.getSeatId());
            }

            // Save booking
            System.out.println("Saving booking to database...");
            Booking savedBooking = bookingRepository.save(booking);
            System.out.println("Booking saved successfully with ID: " + savedBooking.getBookingId());
            
            // Send confirmation email (non-blocking)
            try {
                emailService.sendBookingConfirmation(email, savedBooking.getBookingId().toString(),
                        showtime.getMovie().getTitle(), showtime.getStartTime().toString());
                System.out.println("Confirmation email sent");
            } catch (Exception emailError) {
                System.err.println("Failed to send confirmation email: " + emailError.getMessage());
                // Don't fail the booking for email issues
            }
            
            return savedBooking;
        } catch (IllegalArgumentException e) {
            System.err.println("=== BOOKING SERVICE VALIDATION ERROR: createBooking ===");
            System.err.println("Error Message: " + e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: createBooking ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Email: " + email);
            System.err.println("Showtime ID: " + showtimeId);
            System.err.println("Seat IDs: " + seatIds);
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END BOOKING SERVICE ERROR ===");
            throw new RuntimeException("Failed to create booking: " + e.getMessage());
        }
    }

    public List<Seat> getAvailableSeats(Integer showtimeId) {
        System.out.println("=== BOOKING SERVICE: getAvailableSeats ===");
        System.out.println("Showtime ID: " + showtimeId);
        
        try {
            // Input validation
            if (showtimeId == null || showtimeId <= 0) {
                throw new IllegalArgumentException("Invalid showtime ID: " + showtimeId);
            }
            
            System.out.println("Looking up showtime with ID: " + showtimeId);
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + showtimeId));
            System.out.println("Found showtime - Start: " + showtime.getStartTime() + 
                             ", Hall ID: " + (showtime.getCinemaHall() != null ? showtime.getCinemaHall().getHallId() : "NULL"));
            
            if (showtime.getCinemaHall() == null) {
                System.err.println("ERROR: Showtime has NULL cinema hall!");
                throw new RuntimeException("Showtime has no associated cinema hall");
            }
            
            Integer hallId = showtime.getCinemaHall().getHallId();
            System.out.println("Querying seats for hall ID: " + hallId + " with status: AVAILABLE");
            
            List<Seat> seats = seatRepository.findByCinemaHall_HallIdAndStatus(hallId, "AVAILABLE");
            System.out.println("Found " + seats.size() + " available seats");
            
            // Validate seats
            for (Seat seat : seats) {
                if (seat.getCinemaHall() == null) {
                    System.err.println("WARNING: Seat " + seat.getSeatId() + " has NULL cinema hall");
                }
                System.out.println("Seat - ID: " + seat.getSeatId() + 
                                 ", Row: " + seat.getRow() + 
                                 ", Number: " + seat.getNumber() + 
                                 ", Status: " + seat.getStatus() +
                                 ", Hall: " + (seat.getCinemaHall() != null ? seat.getCinemaHall().getHallId() : "NULL"));
            }
            
            return seats;
        } catch (IllegalArgumentException e) {
            System.err.println("=== BOOKING SERVICE VALIDATION ERROR: getAvailableSeats ===");
            System.err.println("Error Message: " + e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: getAvailableSeats ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Showtime ID: " + showtimeId);
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END BOOKING SERVICE ERROR ===");
            throw new RuntimeException("Failed to get available seats: " + e.getMessage());
        }
    }

    public Booking getBookingById(Integer id) {
        try {
            System.out.println("=== BOOKING SERVICE: getBookingById ===");
            System.out.println("Booking ID: " + id);
            
            Booking booking = bookingRepository.findById(id).orElse(null);
            if (booking != null) {
                System.out.println("Found booking - Movie: " + 
                    (booking.getShowtime() != null && booking.getShowtime().getMovie() != null ? 
                     booking.getShowtime().getMovie().getTitle() : "NULL"));
            } else {
                System.out.println("Booking not found");
            }
            
            return booking;
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: getBookingById ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get booking details: " + e.getMessage());
        }
    }

    public boolean updateBooking(String email, Integer bookingId, Set<Integer> newSeatIds) {
        try {
            System.out.println("=== BOOKING SERVICE: updateBooking ===");
            System.out.println("Email: " + email);
            System.out.println("Booking ID: " + bookingId);
            System.out.println("New Seat IDs: " + newSeatIds);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Check if the booking belongs to the user
            if (!booking.getUser().getEmail().equals(email)) {
                throw new RuntimeException("You can only update your own bookings");
            }

            // Release old seats
            if (booking.getSeats() != null) {
                for (Seat seat : booking.getSeats()) {
                    seat.setStatus("AVAILABLE");
                    seatRepository.save(seat);
                }
            }

            // Reserve new seats
            Set<Seat> newSeats = new HashSet<>();
            for (Integer seatId : newSeatIds) {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Seat not found"));
                if (!"AVAILABLE".equals(seat.getStatus())) {
                    throw new RuntimeException("Seat " + seatId + " is not available");
                }
                seat.setStatus("RESERVED");
                seatRepository.save(seat);
                newSeats.add(seat);
            }

            // Update booking
            booking.setSeats(newSeats);
            booking.setTotalSeats(newSeatIds.size());
            booking.setTotalAmount(booking.getShowtime().getPrice() * newSeatIds.size());
            booking.setStatus("UPDATED"); // Mark as updated
            bookingRepository.save(booking);
            
            System.out.println("Booking updated successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: updateBooking ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update booking: " + e.getMessage());
        }
    }

    public boolean deleteBooking(String email, Integer bookingId) {
        try {
            System.out.println("=== BOOKING SERVICE: deleteBooking ===");
            System.out.println("Email: " + email);
            System.out.println("Booking ID: " + bookingId);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Check if the booking belongs to the user
            if (!booking.getUser().getEmail().equals(email)) {
                throw new RuntimeException("You can only delete your own bookings");
            }

            // Release seats first
            if (booking.getSeats() != null) {
                for (Seat seat : booking.getSeats()) {
                    seat.setStatus("AVAILABLE");
                    seatRepository.save(seat);
                }
            }

            // Instead of deleting, mark as cancelled to avoid foreign key constraint issues
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            
            System.out.println("Booking cancelled successfully (marked as CANCELLED)");
            return true;
            
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: deleteBooking ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to cancel booking: " + e.getMessage());
        }
    }
    
    public Booking createBookingWithDiscount(String email, Integer showtimeId, Set<Integer> seatIds, 
                                           String discountCode, Integer loyaltyPointsToRedeem) {
        System.out.println("=== BOOKING SERVICE: createBookingWithDiscount ===");
        System.out.println("Email: " + email);
        System.out.println("Showtime ID: " + showtimeId);
        System.out.println("Seat IDs: " + seatIds);
        System.out.println("Discount Code: " + discountCode);
        System.out.println("Loyalty Points: " + loyaltyPointsToRedeem);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found"));

            Set<Seat> seats = new HashSet<>();
            for (Integer seatId : seatIds) {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Seat not found"));
                if (!"AVAILABLE".equals(seat.getStatus())) {
                    throw new RuntimeException("Seat " + seatId + " is not available");
                }
                seats.add(seat);
            }

            Double baseAmount = showtime.getPrice() * seatIds.size();
            Double totalDiscount = 0.0;
            
            // Apply discount code if provided
            if (discountCode != null && !discountCode.trim().isEmpty()) {
                Double discountAmount = discountCodeService.calculateDiscount(discountCode, baseAmount);
                totalDiscount += discountAmount;
                if (discountAmount > 0) {
                    discountCodeService.applyDiscountCode(discountCode, baseAmount);
                }
            }
            
            // Apply loyalty points if provided
            if (loyaltyPointsToRedeem != null && loyaltyPointsToRedeem > 0) {
                Double pointsDiscount = loyaltyPointsService.calculateDiscountFromPoints(email, loyaltyPointsToRedeem);
                if (pointsDiscount > 0) {
                    boolean redeemed = loyaltyPointsService.redeemPoints(email, loyaltyPointsToRedeem);
                    if (redeemed) {
                        totalDiscount += pointsDiscount;
                    }
                }
            }
            
            Double finalAmount = Math.max(0, baseAmount - totalDiscount);
            
            Booking booking = new Booking(
                    LocalDateTime.now(), seatIds.size(), finalAmount, "PENDING", user, showtime, seats
            );

            for (Seat seat : seats) {
                seat.setStatus("RESERVED");
                seatRepository.save(seat);
            }

            Booking savedBooking = bookingRepository.save(booking);
            
            // Add loyalty points for the purchase
            loyaltyPointsService.addPointsForPurchase(email, finalAmount);
            
            try {
                emailService.sendBookingConfirmation(email, savedBooking.getBookingId().toString(),
                        showtime.getMovie().getTitle(), showtime.getStartTime().toString());
            } catch (Exception emailError) {
                System.err.println("Failed to send confirmation email: " + emailError.getMessage());
            }
            
            return savedBooking;
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: createBookingWithDiscount ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Booking> getBookingHistory(String email) {
        return bookingRepository.findByUserEmail(email);
    }
}


