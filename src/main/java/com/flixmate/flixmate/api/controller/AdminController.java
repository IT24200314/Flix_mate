package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.UserStatus;
import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.CinemaHall;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.UserStatusRepository;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.PaymentRepository;
import com.flixmate.flixmate.api.repository.MovieRepository;
import com.flixmate.flixmate.api.repository.SeatRepository;
import com.flixmate.flixmate.api.repository.ShowTimeRepository;
import com.flixmate.flixmate.api.repository.CinemaHallRepository;
import com.flixmate.flixmate.api.service.ReportService;
import com.flixmate.flixmate.api.service.NotificationService;
import com.flixmate.flixmate.api.service.MovieManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserStatusRepository userStatusRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ShowTimeRepository showTimeRepository;
    
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    
    @Autowired
    private MovieManagementService movieManagementService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch users: " + e.getMessage());
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {
        try {
            List<com.flixmate.flixmate.api.entity.Booking> bookings = bookingRepository.findAll();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch bookings: " + e.getMessage());
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue() {
        try {
            // Calculate total revenue from payments
            Double totalRevenue = paymentRepository.findAll().stream()
                    .filter(payment -> "SUCCESS".equals(payment.getStatus()))
                    .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
                    .sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("revenue", totalRevenue);
            response.put("total", totalRevenue);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to calculate revenue: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // User stats
            long totalUsers = userRepository.count();
            long adminUsers = userRepository.findAll().stream()
                    .filter(user -> user.getStatus() != null && "admin".equals(user.getStatus().getStatusName()))
                    .count();
            
            // Booking stats
            long totalBookings = bookingRepository.count();
            long confirmedBookings = bookingRepository.findAll().stream()
                    .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                    .count();
            
            // Revenue stats
            Double totalRevenue = paymentRepository.findAll().stream()
                    .filter(payment -> "SUCCESS".equals(payment.getStatus()))
                    .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
                    .sum();
            
            stats.put("totalUsers", totalUsers);
            stats.put("adminUsers", adminUsers);
            stats.put("regularUsers", totalUsers - adminUsers);
            stats.put("totalBookings", totalBookings);
            stats.put("confirmedBookings", confirmedBookings);
            stats.put("totalRevenue", totalRevenue);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch admin stats: " + e.getMessage());
        }
    }

    @GetMapping("/movies")
    public ResponseEntity<?> getAllMovies() {
        try {
            List<Movie> movies = movieRepository.findAll();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch movies: " + e.getMessage());
        }
    }

    @PostMapping("/movies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMovie(@RequestBody Movie movie) {
        try {
            Movie savedMovie = movieRepository.save(movie);
            
            // Create notification for new movie
            notificationService.notifyMovieAdded(savedMovie.getTitle());
            
            return ResponseEntity.ok(savedMovie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create movie",
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMovie(@PathVariable Integer movieId, @RequestBody Movie movie) {
        try {
            if (!movieRepository.existsById(movieId)) {
                return ResponseEntity.notFound().build();
            }
            movie.setMovieId(movieId);
            Movie savedMovie = movieRepository.save(movie);
            return ResponseEntity.ok(savedMovie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update movie",
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer movieId) {
        try {
            if (!movieRepository.existsById(movieId)) {
                return ResponseEntity.notFound().build();
            }
            
            // Use MovieManagementService for proper cascading delete
            boolean deleted = movieManagementService.deleteMovie(movieId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Movie deleted successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to delete movie",
                    "message", "Movie deletion failed"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to delete movie",
                "message", e.getMessage()
            ));
        }
    }


    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch payments: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardData() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Basic counts
            dashboard.put("users", userRepository.count());
            dashboard.put("bookings", bookingRepository.count());
            dashboard.put("payments", paymentRepository.count());
            
            // Recent activity (last 10 bookings)
            List<com.flixmate.flixmate.api.entity.Booking> recentBookings = bookingRepository.findAll().stream()
                    .limit(10)
                    .toList();
            dashboard.put("recentBookings", recentBookings);
            
            // Revenue summary
            Double totalRevenue = paymentRepository.findAll().stream()
                    .filter(payment -> "SUCCESS".equals(payment.getStatus()))
                    .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
                    .sum();
            dashboard.put("totalRevenue", totalRevenue);
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch dashboard data: " + e.getMessage());
        }
    }

    // Test endpoint without authentication for debugging
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        try {
            Map<String, Object> testData = new HashMap<>();
            testData.put("message", "Admin API is working");
            testData.put("timestamp", System.currentTimeMillis());
            testData.put("userCount", userRepository.count());
            testData.put("bookingCount", bookingRepository.count());
            testData.put("movieCount", movieRepository.count());
            
            return ResponseEntity.ok(testData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Test failed: " + e.getMessage());
        }
    }

    // Test endpoint for user statistics without authentication
    @GetMapping("/users/test-stats")
    public ResponseEntity<?> testUserStatistics() {
        try {
            System.out.println("=== TEST USER STATISTICS API CALL START ===");
            
            List<User> allUsers = userRepository.findAll();
            
            // Count users by status
            long totalUsers = allUsers.size();
            long activeUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Active".equals(user.getStatus().getStatusName()))
                    .count();
            long adminUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Admin".equals(user.getStatus().getStatusName()))
                    .count();
            long suspendedUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Suspended".equals(user.getStatus().getStatusName()))
                    .count();
            long inactiveUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Inactive".equals(user.getStatus().getStatusName()))
                    .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            statistics.put("activeUsers", activeUsers);
            statistics.put("adminUsers", adminUsers);
            statistics.put("suspendedUsers", suspendedUsers);
            statistics.put("inactiveUsers", inactiveUsers);
            statistics.put("regularUsers", activeUsers);
            
            System.out.println("Test user statistics: Total=" + totalUsers + ", Active=" + activeUsers + 
                             ", Admin=" + adminUsers + ", Suspended=" + suspendedUsers + ", Inactive=" + inactiveUsers);
            System.out.println("=== TEST USER STATISTICS API CALL END ===");
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("=== TEST USER STATISTICS API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END TEST USER STATISTICS API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to get test user statistics",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    // Simple role change endpoint without authentication for testing
    @PutMapping("/users/{userId}/change-role")
    public ResponseEntity<?> changeUserRoleSimple(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        try {
            System.out.println("=== SIMPLE ROLE CHANGE API CALL START ===");
            System.out.println("User ID: " + userId);
            System.out.println("Request: " + request);
            
            String newStatusName = request.get("statusName");
            if (newStatusName == null || newStatusName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Status name is required",
                    "message", "Please provide a valid status name"
                ));
            }
            
            // Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Find the new status
            UserStatus newStatus = userStatusRepository.findByStatusName(newStatusName)
                    .orElseThrow(() -> new RuntimeException("Status not found: " + newStatusName));
            
            // Update user status
            user.setStatus(newStatus);
            User updatedUser = userRepository.save(user);
            
            System.out.println("User role changed successfully: " + user.getEmail() + " -> " + newStatusName);
            System.out.println("=== SIMPLE ROLE CHANGE API CALL END ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User role updated successfully");
            response.put("userId", updatedUser.getUserId());
            response.put("newStatus", newStatusName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== SIMPLE ROLE CHANGE API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END SIMPLE ROLE CHANGE API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to change user role",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    // ========== SEAT AVAILABILITY ENDPOINTS ==========

    @GetMapping("/seats/availability/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSeatAvailability(@PathVariable Integer showtimeId) {
        try {
            System.out.println("=== ADMIN SEAT AVAILABILITY API CALL START ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            // Get showtime details
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + showtimeId));
            
            // Get all seats for the cinema hall
            List<Seat> allSeats = seatRepository.findByCinemaHall(showtime.getCinemaHall());
            
            // Get bookings for this showtime
            List<com.flixmate.flixmate.api.entity.Booking> bookings = bookingRepository.findByShowtime(showtime);
            
            // Calculate seat statistics
            int totalSeats = allSeats.size();
            int availableSeats = (int) allSeats.stream()
                    .filter(seat -> "AVAILABLE".equals(seat.getStatus()))
                    .count();
            int occupiedSeats = (int) allSeats.stream()
                    .filter(seat -> "OCCUPIED".equals(seat.getStatus()))
                    .count();
            int reservedSeats = (int) allSeats.stream()
                    .filter(seat -> "RESERVED".equals(seat.getStatus()))
                    .count();
            int maintenanceSeats = (int) allSeats.stream()
                    .filter(seat -> "MAINTENANCE".equals(seat.getStatus()))
                    .count();
            
            // Calculate availability percentage
            double availabilityPercentage = totalSeats > 0 ? 
                (double) availableSeats / totalSeats * 100 : 0;
            
            Map<String, Object> response = new HashMap<>();
            response.put("showtimeId", showtimeId);
            response.put("showtime", showtime);
            response.put("totalSeats", totalSeats);
            response.put("availableSeats", availableSeats);
            response.put("occupiedSeats", occupiedSeats);
            response.put("reservedSeats", reservedSeats);
            response.put("maintenanceSeats", maintenanceSeats);
            response.put("availabilityPercentage", Math.round(availabilityPercentage * 100.0) / 100.0);
            response.put("isAvailable", availableSeats > 0);
            response.put("seats", allSeats);
            response.put("bookings", bookings);
            response.put("timestamp", System.currentTimeMillis());
            
            System.out.println("Seat availability: " + availableSeats + "/" + totalSeats + 
                             " (Available/Occupied/Reserved/Maintenance: " + 
                             availableSeats + "/" + occupiedSeats + "/" + reservedSeats + "/" + maintenanceSeats + ")");
            System.out.println("=== ADMIN SEAT AVAILABILITY API CALL END ===");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== ADMIN SEAT AVAILABILITY API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN SEAT AVAILABILITY API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to get seat availability",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/seats/hall/{hallId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getHallSeatLayout(@PathVariable Integer hallId) {
        try {
            System.out.println("=== ADMIN HALL SEAT LAYOUT API CALL START ===");
            System.out.println("Hall ID: " + hallId);
            
            CinemaHall hall = cinemaHallRepository.findById(hallId)
                    .orElseThrow(() -> new RuntimeException("Cinema hall not found with ID: " + hallId));
            
            List<Seat> seats = seatRepository.findByCinemaHall(hall);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hallId", hallId);
            response.put("hall", hall);
            response.put("seats", seats);
            response.put("totalSeats", seats.size());
            response.put("timestamp", System.currentTimeMillis());
            
            System.out.println("Retrieved " + seats.size() + " seats for hall " + hallId);
            System.out.println("=== ADMIN HALL SEAT LAYOUT API CALL END ===");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== ADMIN HALL SEAT LAYOUT API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN HALL SEAT LAYOUT API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to get hall seat layout",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/seats/refresh/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refreshSeatAvailability(@PathVariable Integer showtimeId) {
        try {
            System.out.println("=== ADMIN REFRESH SEAT AVAILABILITY API CALL START ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            // This is essentially the same as getSeatAvailability but with explicit refresh intent
            return getSeatAvailability(showtimeId);
        } catch (Exception e) {
            System.err.println("=== ADMIN REFRESH SEAT AVAILABILITY API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN REFRESH SEAT AVAILABILITY API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to refresh seat availability",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    // ========== USER ROLE MANAGEMENT ENDPOINTS ==========

    @GetMapping("/users/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserStatistics() {
        try {
            System.out.println("=== ADMIN USER STATISTICS API CALL START ===");
            
            List<User> allUsers = userRepository.findAll();
            
            // Count users by status
            long totalUsers = allUsers.size();
            long activeUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Active".equals(user.getStatus().getStatusName()))
                    .count();
            long adminUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Admin".equals(user.getStatus().getStatusName()))
                    .count();
            long suspendedUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Suspended".equals(user.getStatus().getStatusName()))
                    .count();
            long inactiveUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null && "Inactive".equals(user.getStatus().getStatusName()))
                    .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            statistics.put("activeUsers", activeUsers);
            statistics.put("adminUsers", adminUsers);
            statistics.put("suspendedUsers", suspendedUsers);
            statistics.put("inactiveUsers", inactiveUsers);
            statistics.put("regularUsers", activeUsers); // Active users are regular users
            
            System.out.println("User statistics: Total=" + totalUsers + ", Active=" + activeUsers + 
                             ", Admin=" + adminUsers + ", Suspended=" + suspendedUsers + ", Inactive=" + inactiveUsers);
            System.out.println("=== ADMIN USER STATISTICS API CALL END ===");
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("=== ADMIN USER STATISTICS API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN USER STATISTICS API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to get user statistics",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRole(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        try {
            System.out.println("=== ADMIN CHANGE USER ROLE API CALL START ===");
            System.out.println("User ID: " + userId);
            System.out.println("Request: " + request);
            
            String newStatusName = request.get("statusName");
            if (newStatusName == null || newStatusName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Status name is required",
                    "message", "Please provide a valid status name"
                ));
            }
            
            // Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Find the new status
            UserStatus newStatus = userStatusRepository.findByStatusName(newStatusName)
                    .orElseThrow(() -> new RuntimeException("Status not found: " + newStatusName));
            
            // Update user status
            user.setStatus(newStatus);
            User updatedUser = userRepository.save(user);
            
            System.out.println("User role changed successfully: " + user.getEmail() + " -> " + newStatusName);
            System.out.println("=== ADMIN CHANGE USER ROLE API CALL END ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User role updated successfully");
            response.put("userId", updatedUser.getUserId());
            response.put("newStatus", newStatusName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== ADMIN CHANGE USER ROLE API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN CHANGE USER ROLE API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to change user role",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserStatus(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        try {
            System.out.println("=== ADMIN CHANGE USER STATUS API CALL START ===");
            System.out.println("User ID: " + userId);
            System.out.println("Request: " + request);
            
            String newStatusName = request.get("statusName");
            if (newStatusName == null || newStatusName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Status name is required",
                    "message", "Please provide a valid status name"
                ));
            }
            
            // Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Find the new status
            UserStatus newStatus = userStatusRepository.findByStatusName(newStatusName)
                    .orElseThrow(() -> new RuntimeException("Status not found: " + newStatusName));
            
            // Update user status
            user.setStatus(newStatus);
            User updatedUser = userRepository.save(user);
            
            System.out.println("User status changed successfully: " + user.getEmail() + " -> " + newStatusName);
            System.out.println("=== ADMIN CHANGE USER STATUS API CALL END ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User status updated successfully");
            response.put("userId", updatedUser.getUserId());
            response.put("newStatus", newStatusName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== ADMIN CHANGE USER STATUS API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN CHANGE USER STATUS API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to change user status",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserDetails(@PathVariable Integer userId) {
        try {
            System.out.println("=== ADMIN GET USER DETAILS API CALL START ===");
            System.out.println("User ID: " + userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("userId", user.getUserId());
            userDetails.put("userName", user.getUserName());
            userDetails.put("email", user.getEmail());
            userDetails.put("phone", user.getPhone());
            userDetails.put("status", user.getStatus() != null ? user.getStatus().getStatusName() : "Unknown");
            userDetails.put("role", user.getStatus() != null ? user.getStatus().getRole() : "Unknown");
            userDetails.put("registrationDate", user.getRegistrationDate());
            userDetails.put("lastLogin", user.getLastLogin());
            
            System.out.println("User details retrieved: " + user.getEmail());
            System.out.println("=== ADMIN GET USER DETAILS API CALL END ===");
            
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            System.err.println("=== ADMIN GET USER DETAILS API ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ADMIN GET USER DETAILS API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to get user details",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}
