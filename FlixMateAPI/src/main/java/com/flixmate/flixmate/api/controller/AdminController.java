package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.Payment;
import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.CinemaHall;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.PaymentRepository;
import com.flixmate.flixmate.api.repository.MovieRepository;
import com.flixmate.flixmate.api.repository.CinemaHallRepository;
import com.flixmate.flixmate.api.service.ReportService;
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
    private BookingRepository bookingRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    
    @Autowired
    private ReportService reportService;
    
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
            // Set timestamps for new movie
            movie.setCreatedDate(java.time.LocalDateTime.now());
            movie.setUpdatedDate(java.time.LocalDateTime.now());
            if (movie.getIsActive() == null) {
                movie.setIsActive(true);
            }
            
            
            Movie savedMovie = movieRepository.save(movie);
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
            
            // Get the existing movie to preserve the original created_date
            Movie existingMovie = movieRepository.findById(movieId).orElse(null);
            if (existingMovie != null) {
                // Preserve the original created_date
                movie.setCreatedDate(existingMovie.getCreatedDate());
                movie.setMovieId(movieId);
                movie.setUpdatedDate(java.time.LocalDateTime.now());
                
                
                Movie savedMovie = movieRepository.save(movie);
                return ResponseEntity.ok(savedMovie);
            } else {
                return ResponseEntity.notFound().build();
            }
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
            movieRepository.deleteById(movieId);
            return ResponseEntity.ok(Map.of("message", "Movie deleted successfully"));
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

    // ========== CINEMA HALL MANAGEMENT ==========
    
    @GetMapping("/cinema-halls")
    public ResponseEntity<?> getAllCinemaHalls() {
        try {
            List<CinemaHall> halls = cinemaHallRepository.findAll();
            return ResponseEntity.ok(halls);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch cinema halls: " + e.getMessage());
        }
    }
    
    @PostMapping("/cinema-halls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCinemaHall(@RequestBody Map<String, Object> hallData) {
        try {
            CinemaHall hall = new CinemaHall();
            
            // Parse name
            String name = (String) hallData.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Cinema hall name is required");
            }
            hall.setName(name.trim());
            
            // Parse location
            String location = (String) hallData.get("location");
            hall.setLocation(location != null ? location.trim() : "");
            
            // Parse capacity
            Object capacityObj = hallData.get("capacity");
            if (capacityObj == null) {
                throw new IllegalArgumentException("Cinema hall capacity is required");
            }
            Integer capacity;
            if (capacityObj instanceof Number) {
                capacity = ((Number) capacityObj).intValue();
            } else if (capacityObj instanceof String) {
                capacity = Integer.parseInt((String) capacityObj);
            } else {
                throw new IllegalArgumentException("Invalid capacity format");
            }
            
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be greater than 0");
            }
            hall.setCapacity(capacity);
            
            CinemaHall savedHall = cinemaHallRepository.save(hall);
            return ResponseEntity.ok(savedHall);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create cinema hall: " + e.getMessage());
        }
    }
    
    @PutMapping("/cinema-halls/{hallId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCinemaHall(@PathVariable Integer hallId, @RequestBody Map<String, Object> hallData) {
        try {
            CinemaHall existingHall = cinemaHallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Cinema hall not found with ID: " + hallId));
            
            // Parse name
            String name = (String) hallData.get("name");
            if (name != null && !name.trim().isEmpty()) {
                existingHall.setName(name.trim());
            }
            
            // Parse location
            String location = (String) hallData.get("location");
            if (location != null) {
                existingHall.setLocation(location.trim());
            }
            
            // Parse capacity
            Object capacityObj = hallData.get("capacity");
            if (capacityObj != null) {
                Integer capacity;
                if (capacityObj instanceof Number) {
                    capacity = ((Number) capacityObj).intValue();
                } else if (capacityObj instanceof String) {
                    capacity = Integer.parseInt((String) capacityObj);
                } else {
                    throw new IllegalArgumentException("Invalid capacity format");
                }
                
                if (capacity <= 0) {
                    throw new IllegalArgumentException("Capacity must be greater than 0");
                }
                existingHall.setCapacity(capacity);
            }
            
            CinemaHall updatedHall = cinemaHallRepository.save(existingHall);
            return ResponseEntity.ok(updatedHall);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update cinema hall: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/cinema-halls/{hallId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCinemaHall(@PathVariable Integer hallId) {
        try {
            // Check if hall exists
            if (!cinemaHallRepository.existsById(hallId)) {
                throw new IllegalArgumentException("Cinema hall not found with ID: " + hallId);
            }
            
            // Check if hall has showtimes (this would need a different method in the service)
            // For now, we'll allow deletion and let the database constraints handle it
            
            cinemaHallRepository.deleteById(hallId);
            return ResponseEntity.ok(Map.of("message", "Cinema hall deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete cinema hall: " + e.getMessage());
        }
    }

    // ========== SHOWTIME MANAGEMENT ==========
    
    @GetMapping("/movies/{movieId}/showtimes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getShowtimesForMovie(@PathVariable Integer movieId) {
        try {
            List<ShowTime> showtimes = movieManagementService.getShowtimesForMovie(movieId);
            return ResponseEntity.ok(showtimes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch showtimes: " + e.getMessage());
        }
    }
    
    @PostMapping("/movies/{movieId}/showtimes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createShowtime(@PathVariable Integer movieId, @RequestBody Map<String, Object> showtimeData) {
        try {
            ShowTime showtime = new ShowTime();
            
            // Parse start time
            String startTimeStr = (String) showtimeData.get("startTime");
            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                // Expecting format: "2024-01-01T14:30"
                showtime.setStartTime(java.time.LocalDateTime.parse(startTimeStr));
            }
            
            // Parse price
            Object priceObj = showtimeData.get("price");
            if (priceObj != null) {
                if (priceObj instanceof Number) {
                    showtime.setPrice(((Number) priceObj).doubleValue());
                } else if (priceObj instanceof String) {
                    showtime.setPrice(Double.parseDouble((String) priceObj));
                }
            }
            
            // Parse cinema hall
            Object hallIdObj = showtimeData.get("hallId");
            if (hallIdObj != null) {
                Integer hallId;
                if (hallIdObj instanceof Number) {
                    hallId = ((Number) hallIdObj).intValue();
                } else if (hallIdObj instanceof String) {
                    hallId = Integer.parseInt((String) hallIdObj);
                } else {
                    throw new IllegalArgumentException("Invalid hall ID format");
                }
                
                CinemaHall hall = cinemaHallRepository.findById(hallId)
                    .orElseThrow(() -> new IllegalArgumentException("Cinema hall not found with ID: " + hallId));
                showtime.setCinemaHall(hall);
            }
            
            ShowTime createdShowtime = movieManagementService.createShowtime(movieId, showtime);
            return ResponseEntity.ok(createdShowtime);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create showtime: " + e.getMessage());
        }
    }
    
    @PutMapping("/showtimes/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShowtime(@PathVariable Integer showtimeId, @RequestBody Map<String, Object> showtimeData) {
        try {
            ShowTime showtime = new ShowTime();
            showtime.setShowtimeId(showtimeId);
            
            // Parse start time
            String startTimeStr = (String) showtimeData.get("startTime");
            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                showtime.setStartTime(java.time.LocalDateTime.parse(startTimeStr));
            }
            
            // Parse price
            Object priceObj = showtimeData.get("price");
            if (priceObj != null) {
                if (priceObj instanceof Number) {
                    showtime.setPrice(((Number) priceObj).doubleValue());
                } else if (priceObj instanceof String) {
                    showtime.setPrice(Double.parseDouble((String) priceObj));
                }
            }
            
            // Parse cinema hall
            Object hallIdObj = showtimeData.get("hallId");
            if (hallIdObj != null) {
                Integer hallId;
                if (hallIdObj instanceof Number) {
                    hallId = ((Number) hallIdObj).intValue();
                } else if (hallIdObj instanceof String) {
                    hallId = Integer.parseInt((String) hallIdObj);
                } else {
                    throw new IllegalArgumentException("Invalid hall ID format");
                }
                
                CinemaHall hall = cinemaHallRepository.findById(hallId)
                    .orElseThrow(() -> new IllegalArgumentException("Cinema hall not found with ID: " + hallId));
                showtime.setCinemaHall(hall);
            }
            
            ShowTime updatedShowtime = movieManagementService.updateShowtime(showtime);
            return ResponseEntity.ok(updatedShowtime);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update showtime: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/showtimes/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShowtime(@PathVariable Integer showtimeId) {
        try {
            boolean deleted = movieManagementService.deleteShowtime(showtimeId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Showtime deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete showtime: " + e.getMessage());
        }
    }

    // Initialize database with sample data
    @PostMapping("/init-data")
    public ResponseEntity<?> initializeData() {
        try {
            // This endpoint will be used to initialize the database with sample data
            // For now, we'll just return a message indicating the data should be loaded
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Database initialization endpoint called");
            response.put("timestamp", System.currentTimeMillis());
            response.put("userCount", userRepository.count());
            response.put("movieCount", movieRepository.count());
            response.put("cinemaHallCount", cinemaHallRepository.count());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Initialization failed: " + e.getMessage());
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
}
