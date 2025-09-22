package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        return bookingRepository.findByUserEmail(email);
    }

    public Booking createBooking(String email, Integer showtimeId, Set<Integer> seatIds) {
        System.out.println("=== BOOKING SERVICE: createBooking ===");
        System.out.println("Email: " + email);
        System.out.println("Showtime ID: " + showtimeId);
        System.out.println("Seat IDs: " + seatIds);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Found user: " + user.getEmail() + " (ID: " + user.getUserId() + ")");
            
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found"));
            System.out.println("Found showtime: " + showtime.getShowtimeId() + 
                             " (Movie: " + (showtime.getMovie() != null ? showtime.getMovie().getTitle() : "NULL") + 
                             ", Hall: " + (showtime.getCinemaHall() != null ? showtime.getCinemaHall().getHallId() : "NULL") + ")");

            Set<Seat> seats = new HashSet<>();
            for (Integer seatId : seatIds) {
                System.out.println("Looking for seat with ID: " + seatId);
                Seat seat = seatRepository.findById(seatId).orElse(null);
                
                if (seat == null) {
                    // Seat doesn't exist in database, create it dynamically
                    System.out.println("Seat not found in database, creating dynamically for seat ID: " + seatId);
                    seat = createDynamicSeat(seatId, showtime.getCinemaHall());
                    if (seat == null) {
                        throw new RuntimeException("Unable to create seat with ID: " + seatId + ". Please refresh the page and select seats again.");
                    }
                }
                
                if (!"AVAILABLE".equals(seat.getStatus())) {
                    throw new RuntimeException("Seat " + seatId + " (Row: " + seat.getRow() + seat.getNumber() + ") is not available");
                }
                seats.add(seat);
                System.out.println("Added seat: " + seatId + " (Row: " + seat.getRow() + ", Number: " + seat.getNumber() + ")");
            }

            Double totalAmount = showtime.getPrice() * seatIds.size();
            System.out.println("Total amount: " + totalAmount);
            
            Booking booking = new Booking(
                    LocalDateTime.now(), seatIds.size(), totalAmount, "PENDING", user, showtime, seats
            );
            System.out.println("Created booking object");

            for (Seat seat : seats) {
                seat.setStatus("RESERVED");
                seatRepository.save(seat);
                System.out.println("Reserved seat: " + seat.getSeatId());
            }

            System.out.println("Saving booking to database...");
            Booking savedBooking = bookingRepository.save(booking);
            System.out.println("Booking saved successfully with ID: " + savedBooking.getBookingId());
            
            try {
                emailService.sendBookingConfirmation(email, savedBooking.getBookingId().toString(),
                        showtime.getMovie().getTitle(), showtime.getStartTime().toString());
                System.out.println("Confirmation email sent");
            } catch (Exception emailError) {
                System.err.println("Failed to send confirmation email: " + emailError.getMessage());
                // Don't fail the booking for email issues
            }
            
            return savedBooking;
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
            throw e;
        }
    }

    public List<Seat> getAvailableSeats(Integer showtimeId) {
        System.out.println("=== BOOKING SERVICE: getAvailableSeats ===");
        System.out.println("Showtime ID: " + showtimeId);
        
        try {
            System.out.println("Looking up showtime with ID: " + showtimeId);
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found"));
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
            
            // If no seats found, create them dynamically
            if (seats.isEmpty()) {
                System.out.println("No seats found in database, creating them dynamically for hall ID: " + hallId);
                seats = createSeatsForHall(showtime.getCinemaHall());
                System.out.println("Created " + seats.size() + " seats dynamically");
            }
            
            // Log each seat
            for (Seat seat : seats) {
                System.out.println("Seat - ID: " + seat.getSeatId() + 
                                 ", Row: " + seat.getRow() + 
                                 ", Number: " + seat.getNumber() + 
                                 ", Status: " + seat.getStatus() +
                                 ", Hall: " + (seat.getCinemaHall() != null ? seat.getCinemaHall().getHallId() : "NULL"));
            }
            
            return seats;
        } catch (Exception e) {
            System.err.println("=== BOOKING SERVICE ERROR: getAvailableSeats ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Showtime ID: " + showtimeId);
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END BOOKING SERVICE ERROR ===");
            throw e;
        }
    }
    
    private Seat createDynamicSeat(Integer seatId, CinemaHall cinemaHall) {
        try {
            // Calculate row and number from seat ID
            // Assuming seat ID format: hallId * 1000 + seatNumber
            int seatNumber = seatId % 1000;
            int rowIndex = (seatNumber - 1) / 10; // Assuming 10 seats per row
            int seatInRow = ((seatNumber - 1) % 10) + 1;
            
            String row = String.valueOf((char) ('A' + rowIndex));
            
            Seat seat = new Seat();
            seat.setSeatId(seatId);
            seat.setRow(row);
            seat.setNumber(seatInRow);
            seat.setStatus("AVAILABLE");
            seat.setCinemaHall(cinemaHall);
            
            // Save the seat to database
            seat = seatRepository.save(seat);
            System.out.println("Created dynamic seat: " + seatId + " (Row: " + row + ", Number: " + seatInRow + ")");
            return seat;
        } catch (Exception e) {
            System.err.println("Error creating dynamic seat: " + e.getMessage());
            return null;
        }
    }
    
    private List<Seat> createSeatsForHall(CinemaHall cinemaHall) {
        List<Seat> seats = new ArrayList<>();
        int capacity = cinemaHall.getCapacity() != null ? cinemaHall.getCapacity() : 100;
        int seatsPerRow = 10;
        int totalRows = (int) Math.ceil((double) capacity / seatsPerRow);
        
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            String row = String.valueOf((char) ('A' + rowIndex));
            int seatsInThisRow = Math.min(seatsPerRow, capacity - (rowIndex * seatsPerRow));
            
            for (int seatNum = 1; seatNum <= seatsInThisRow; seatNum++) {
                int seatId = cinemaHall.getHallId() * 1000 + (rowIndex * seatsPerRow + seatNum);
                
                Seat seat = new Seat();
                seat.setSeatId(seatId);
                seat.setRow(row);
                seat.setNumber(seatNum);
                seat.setStatus("AVAILABLE");
                seat.setCinemaHall(cinemaHall);
                
                seats.add(seat);
            }
        }
        
        // Save all seats to database
        seats = seatRepository.saveAll(seats);
        System.out.println("Created " + seats.size() + " seats for hall " + cinemaHall.getHallId());
        return seats;
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


