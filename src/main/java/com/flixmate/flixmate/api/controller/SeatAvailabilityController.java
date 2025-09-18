package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatAvailabilityController {
    
    @Autowired
    private BookingService bookingService;
    
    @GetMapping("/available/{showtimeId}")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Integer showtimeId) {
        try {
            List<Seat> seats = bookingService.getAvailableSeats(showtimeId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/refresh/{showtimeId}")
    public ResponseEntity<List<Seat>> refreshSeatAvailability(@PathVariable Integer showtimeId) {
        try {
            List<Seat> seats = bookingService.getAvailableSeats(showtimeId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
