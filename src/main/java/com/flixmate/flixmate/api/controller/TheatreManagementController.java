package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.StaffSchedule;
import com.flixmate.flixmate.api.service.TheatreManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/theatre")
public class TheatreManagementController {

    @Autowired
    private TheatreManagementService theatreManagementService;

    @GetMapping("/seats/{hallId}")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Integer hallId) {
        return ResponseEntity.ok(theatreManagementService.getAvailableSeatsByHall(hallId));
    }

    @PostMapping("/showtimes")
    public ResponseEntity<ShowTime> addShowTime(@RequestParam Integer hallId,
                                              @RequestParam String startTime,
                                              @RequestParam String endTime,
                                              @RequestParam Double price,
                                              @RequestParam Integer movieId) {
        return ResponseEntity.ok(theatreManagementService.addShowTime(hallId, startTime, endTime, price, movieId));
    }

    @PostMapping("/staff-schedules")
    public ResponseEntity<StaffSchedule> addStaffSchedule(@RequestParam String staffName,
                                                        @RequestParam String startTime,
                                                        @RequestParam String endTime,
                                                        @RequestParam Integer hallId) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        return ResponseEntity.ok(theatreManagementService.addStaffSchedule(staffName, start, end, hallId));
    }

    @GetMapping("/staff-schedules/{hallId}")
    public ResponseEntity<List<StaffSchedule>> getStaffSchedules(@PathVariable Integer hallId) {
        return ResponseEntity.ok(theatreManagementService.getStaffSchedulesByHall(hallId));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<?> getShowtimesByMovie(@RequestParam(required = false) Integer movieId) {
        System.out.println("=== SHOWTIMES API CALL START ===");
        System.out.println("Requested movieId: " + movieId);
        
        try {
            if (movieId != null) {
                System.out.println("Fetching showtimes for movie ID: " + movieId);
                List<ShowTime> showtimes = theatreManagementService.getShowtimesByMovie(movieId);
                System.out.println("Found " + showtimes.size() + " showtimes");
                
                if (showtimes.isEmpty()) {
                    System.out.println("No showtimes found for movie ID: " + movieId);
                    return ResponseEntity.ok().body("No showtimes found for movie ID: " + movieId);
                }
                
                // Log each showtime details
                for (ShowTime st : showtimes) {
                    System.out.println("Showtime ID: " + st.getShowtimeId() + 
                                     ", Start: " + st.getStartTime() + 
                                     ", End: " + st.getEndTime() + 
                                     ", Price: " + st.getPrice());
                }
                
                return ResponseEntity.ok(showtimes);
            } else {
                System.out.println("Fetching all showtimes");
                List<ShowTime> allShowtimes = theatreManagementService.getAllShowtimes();
                System.out.println("Found " + allShowtimes.size() + " total showtimes");
                return ResponseEntity.ok(allShowtimes);
            }
        } catch (Exception e) {
            System.err.println("=== SHOWTIMES API ERROR ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Movie ID: " + movieId);
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END SHOWTIMES API ERROR ===");
            
            return ResponseEntity.badRequest().body("Failed to fetch showtimes: " + e.getMessage());
        } finally {
            System.out.println("=== SHOWTIMES API CALL END ===");
        }
    }
}
