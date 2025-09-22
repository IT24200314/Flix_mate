package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<String> getAllBookings() {
        return ResponseEntity.badRequest().body("Please specify a showtime ID. Use /api/bookings/{showtimeId} to create a booking or /api/bookings/user to get user bookings.");
    }

    @GetMapping("/user")
    public ResponseEntity<List<Booking>> getUserBookings(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getUserBookings(userDetails.getUsername()));
    }

    @PostMapping("/{showtimeId}")
    public ResponseEntity<?> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Integer showtimeId,
                                           @RequestBody Set<Integer> seatIds) {
        System.out.println("=== BOOKING CONTROLLER: createBooking ===");
        System.out.println("User: " + (userDetails != null ? userDetails.getUsername() : "NULL"));
        System.out.println("Showtime ID: " + showtimeId);
        System.out.println("Seat IDs: " + seatIds);
        
        if (userDetails == null) {
            System.err.println("ERROR: User not authenticated");
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        try {
            Booking booking = bookingService.createBooking(userDetails.getUsername(), showtimeId, seatIds);
            System.out.println("Booking created successfully with ID: " + booking.getBookingId());
            return ResponseEntity.ok(booking);
        } catch (Exception ex) {
            System.err.println("=== BOOKING CONTROLLER ERROR: createBooking ===");
            System.err.println("Error Type: " + ex.getClass().getSimpleName());
            System.err.println("Error Message: " + ex.getMessage());
            System.err.println("User: " + userDetails.getUsername());
            System.err.println("Showtime ID: " + showtimeId);
            System.err.println("Seat IDs: " + seatIds);
            System.err.println("Stack Trace:");
            ex.printStackTrace();
            System.err.println("=== END BOOKING CONTROLLER ERROR ===");
            
            // Return more specific error messages
            String errorMessage = ex.getMessage();
            if (ex.getMessage().contains("FOREIGN KEY constraint")) {
                errorMessage = "Database constraint error: " + ex.getMessage();
            } else if (ex.getMessage().contains("could not execute statement")) {
                errorMessage = "Database execution error: " + ex.getMessage();
            }
            
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @GetMapping("/available/{showtimeId}")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Integer showtimeId) {
        System.out.println("=== AVAILABLE SEATS API CALL START ===");
        System.out.println("Requested showtimeId: " + showtimeId);
        
        try {
            System.out.println("Fetching available seats for showtime ID: " + showtimeId);
            List<Seat> seats = bookingService.getAvailableSeats(showtimeId);
            System.out.println("Found " + seats.size() + " available seats");
            
            // Log seat details
            for (Seat seat : seats) {
                System.out.println("Seat ID: " + seat.getSeatId() + 
                                 ", Row: " + seat.getRow() + 
                                 ", Number: " + seat.getNumber() + 
                                 ", Status: " + seat.getStatus());
            }
            
            return ResponseEntity.ok(seats);
        } catch (Exception ex) {
            System.err.println("=== AVAILABLE SEATS API ERROR ===");
            System.err.println("Error Type: " + ex.getClass().getSimpleName());
            System.err.println("Error Message: " + ex.getMessage());
            System.err.println("Showtime ID: " + showtimeId);
            System.err.println("Stack Trace:");
            ex.printStackTrace();
            System.err.println("=== END AVAILABLE SEATS API ERROR ===");
            
            return ResponseEntity.badRequest().body("Failed to fetch available seats: " + ex.getMessage());
        } finally {
            System.out.println("=== AVAILABLE SEATS API CALL END ===");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        try {
            System.out.println("=== BOOKING CONTROLLER: getBookingById ===");
            System.out.println("Booking ID: " + id);
            
            Booking booking = bookingService.getBookingById(id);
            if (booking != null) {
                System.out.println("Returning booking details");
                return ResponseEntity.ok(booking);
            } else {
                System.out.println("Booking not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            System.err.println("=== BOOKING CONTROLLER ERROR: getBookingById ===");
            System.err.println("Error Type: " + ex.getClass().getSimpleName());
            System.err.println("Error Message: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get booking details: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBooking(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Integer id,
                                              @RequestBody Set<Integer> seatIds) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            boolean updated = bookingService.updateBooking(userDetails.getUsername(), id, seatIds);
            if (updated) {
                return ResponseEntity.ok("Booking updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Integer id) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            boolean deleted = bookingService.deleteBooking(userDetails.getUsername(), id);
            if (deleted) {
                return ResponseEntity.ok("Booking deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    @PostMapping("/{showtimeId}/with-discount")
    public ResponseEntity<?> createBookingWithDiscount(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable Integer showtimeId,
                                                      @RequestBody BookingRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        try {
            Booking booking = bookingService.createBookingWithDiscount(
                userDetails.getUsername(), 
                showtimeId, 
                request.getSeatIds(),
                request.getDiscountCode(),
                request.getLoyaltyPointsToRedeem()
            );
            return ResponseEntity.ok(booking);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error creating booking: " + ex.getMessage());
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<Booking>> getBookingHistory(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getBookingHistory(userDetails.getUsername()));
    }
    
    // Inner class for booking request
    public static class BookingRequest {
        private Set<Integer> seatIds;
        private String discountCode;
        private Integer loyaltyPointsToRedeem;
        
        // Getters and setters
        public Set<Integer> getSeatIds() { return seatIds; }
        public void setSeatIds(Set<Integer> seatIds) { this.seatIds = seatIds; }
        
        public String getDiscountCode() { return discountCode; }
        public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
        
        public Integer getLoyaltyPointsToRedeem() { return loyaltyPointsToRedeem; }
        public void setLoyaltyPointsToRedeem(Integer loyaltyPointsToRedeem) { this.loyaltyPointsToRedeem = loyaltyPointsToRedeem; }
    }
}
