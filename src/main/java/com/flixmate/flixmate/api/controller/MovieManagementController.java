package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.service.MovieManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/movie-management")
@CrossOrigin(origins = "*")
public class MovieManagementController {

    @Autowired
    private MovieManagementService movieManagementService;

    // ========== MOVIE CRUD OPERATIONS ==========

    @GetMapping
    public ResponseEntity<?> getAllMovies() {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getAllMovies ===");
            List<Movie> movies = movieManagementService.getAllMovies();
            System.out.println("Found " + movies.size() + " movies");
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getAllMovies ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch movies: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getMovieById ===");
            System.out.println("Movie ID: " + id);
            Movie movie = movieManagementService.getMovieById(id);
            if (movie != null) {
                System.out.println("Found movie: " + movie.getTitle());
                return ResponseEntity.ok(movie);
            } else {
                System.out.println("Movie not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getMovieById ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch movie: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createMovie(@RequestBody Movie movie) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: createMovie ===");
            System.out.println("Movie Title: " + movie.getTitle());
            System.out.println("Genre: " + movie.getGenre());
            System.out.println("Duration: " + movie.getDuration());
            
            Movie createdMovie = movieManagementService.createMovie(movie);
            System.out.println("Movie created successfully with ID: " + createdMovie.getMovieId());
            return ResponseEntity.ok(createdMovie);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: createMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create movie: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Integer id, @RequestBody Movie movie) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: updateMovie ===");
            System.out.println("Movie ID: " + id);
            System.out.println("New Title: " + movie.getTitle());
            
            movie.setMovieId(id);
            Movie updatedMovie = movieManagementService.updateMovie(movie);
            System.out.println("Movie updated successfully");
            return ResponseEntity.ok(updatedMovie);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: updateMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to update movie: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: deleteMovie ===");
            System.out.println("Movie ID: " + id);
            
            boolean deleted = movieManagementService.deleteMovie(id);
            if (deleted) {
                System.out.println("Movie deleted successfully");
                return ResponseEntity.ok("Movie deleted successfully");
            } else {
                System.out.println("Movie not found or could not be deleted");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: deleteMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to delete movie: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/showtime-count")
    public ResponseEntity<?> getShowtimeCount(@PathVariable Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getShowtimeCount ===");
            System.out.println("Movie ID: " + id);
            
            int count = movieManagementService.getShowtimeCountForMovie(id);
            System.out.println("Showtime count: " + count);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getShowtimeCount ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get showtime count: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveMovie(@PathVariable Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: archiveMovie ===");
            System.out.println("Movie ID: " + id);
            
            boolean archived = movieManagementService.archiveMovie(id);
            if (archived) {
                System.out.println("Movie archived successfully");
                return ResponseEntity.ok("Movie archived successfully");
            } else {
                System.out.println("Movie not found or could not be archived");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: archiveMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to archive movie: " + e.getMessage());
        }
    }

    // ========== SHOWTIME MANAGEMENT ==========

    @GetMapping("/{movieId}/showtimes")
    public ResponseEntity<?> getShowtimesForMovie(@PathVariable Integer movieId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getShowtimesForMovie ===");
            System.out.println("Movie ID: " + movieId);
            
            List<ShowTime> showtimes = movieManagementService.getShowtimesForMovie(movieId);
            System.out.println("Found " + showtimes.size() + " showtimes for movie");
            return ResponseEntity.ok(showtimes);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getShowtimesForMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch showtimes: " + e.getMessage());
        }
    }

    @PostMapping("/{movieId}/showtimes")
    public ResponseEntity<?> createShowtime(@PathVariable Integer movieId, @RequestBody ShowTime showtime) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: createShowtime ===");
            System.out.println("Movie ID: " + movieId);
            System.out.println("Showtime: " + showtime.getStartTime());
            
            ShowTime createdShowtime = movieManagementService.createShowtime(movieId, showtime);
            System.out.println("Showtime created successfully with ID: " + createdShowtime.getShowtimeId());
            return ResponseEntity.ok(createdShowtime);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: createShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create showtime: " + e.getMessage());
        }
    }

    @PutMapping("/showtimes/{showtimeId}")
    public ResponseEntity<?> updateShowtime(@PathVariable Integer showtimeId, @RequestBody ShowTime showtime) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: updateShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            showtime.setShowtimeId(showtimeId);
            ShowTime updatedShowtime = movieManagementService.updateShowtime(showtime);
            System.out.println("Showtime updated successfully");
            return ResponseEntity.ok(updatedShowtime);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: updateShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to update showtime: " + e.getMessage());
        }
    }

    @DeleteMapping("/showtimes/{showtimeId}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Integer showtimeId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: deleteShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            boolean deleted = movieManagementService.deleteShowtime(showtimeId);
            if (deleted) {
                System.out.println("Showtime deleted successfully");
                return ResponseEntity.ok("Showtime deleted successfully");
            } else {
                System.out.println("Showtime not found or could not be deleted");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: deleteShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to delete showtime: " + e.getMessage());
        }
    }

    // ========== SEAT BOOKING VIEW ==========

    @GetMapping("/showtimes/{showtimeId}/bookings")
    public ResponseEntity<?> getSeatBookingsForShowtime(@PathVariable Integer showtimeId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getSeatBookingsForShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            Map<String, Object> bookingInfo = movieManagementService.getSeatBookingsForShowtime(showtimeId);
            System.out.println("Retrieved booking information for showtime");
            return ResponseEntity.ok(bookingInfo);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getSeatBookingsForShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch seat bookings: " + e.getMessage());
        }
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<?> getSeatStatusForShowtime(@PathVariable Integer showtimeId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getSeatStatusForShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            List<Seat> seats = movieManagementService.getSeatStatusForShowtime(showtimeId);
            System.out.println("Retrieved " + seats.size() + " seats for showtime");
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getSeatStatusForShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch seat status: " + e.getMessage());
        }
    }

    // ========== STATISTICS AND REPORTS ==========

    @GetMapping("/statistics")
    public ResponseEntity<?> getMovieStatistics() {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getMovieStatistics ===");
            
            Map<String, Object> statistics = movieManagementService.getMovieStatistics();
            System.out.println("Retrieved movie statistics");
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getMovieStatistics ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch statistics: " + e.getMessage());
        }
    }

    @GetMapping("/{movieId}/statistics")
    public ResponseEntity<?> getMovieStatistics(@PathVariable Integer movieId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT: getMovieStatistics ===");
            System.out.println("Movie ID: " + movieId);
            
            Map<String, Object> statistics = movieManagementService.getMovieStatistics(movieId);
            System.out.println("Retrieved statistics for movie");
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT ERROR: getMovieStatistics ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch movie statistics: " + e.getMessage());
        }
    }
}
