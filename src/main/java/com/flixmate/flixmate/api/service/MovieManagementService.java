package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.CinemaHall;
import com.flixmate.flixmate.api.repository.MovieRepository;
import com.flixmate.flixmate.api.repository.ShowTimeRepository;
import com.flixmate.flixmate.api.repository.SeatRepository;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.CinemaHallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieManagementService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    // ========== MOVIE CRUD OPERATIONS ==========

    public List<Movie> getAllMovies() {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getAllMovies ===");
            List<Movie> movies = movieRepository.findAll();
            System.out.println("Retrieved " + movies.size() + " movies from database");
            return movies;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getAllMovies ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve movies: " + e.getMessage());
        }
    }

    public Movie getMovieById(Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getMovieById ===");
            System.out.println("Movie ID: " + id);
            
            Movie movie = movieRepository.findById(id).orElse(null);
            if (movie != null) {
                System.out.println("Found movie: " + movie.getTitle());
            } else {
                System.out.println("Movie not found");
            }
            return movie;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getMovieById ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve movie: " + e.getMessage());
        }
    }

    public Movie createMovie(Movie movie) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: createMovie ===");
            System.out.println("Movie Title: " + movie.getTitle());
            System.out.println("Genre: " + movie.getGenre());
            System.out.println("Duration: " + movie.getDuration());
            
            // Validate required fields
            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie title is required");
            }
            if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie genre is required");
            }
            if (movie.getDuration() == null || movie.getDuration() <= 0) {
                throw new IllegalArgumentException("Movie duration must be greater than 0");
            }

            // Set default values if not provided
            if (movie.getLanguage() == null || movie.getLanguage().trim().isEmpty()) {
                movie.setLanguage("English");
            }
            if (movie.getDirector() == null || movie.getDirector().trim().isEmpty()) {
                movie.setDirector("Unknown");
            }
            if (movie.getDescription() == null || movie.getDescription().trim().isEmpty()) {
                movie.setDescription("No description available");
            }
            if (movie.getReleaseYear() == null) {
                movie.setReleaseYear(2025);
            }

            Movie savedMovie = movieRepository.save(movie);
            System.out.println("Movie created successfully with ID: " + savedMovie.getMovieId());
            return savedMovie;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: createMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create movie: " + e.getMessage());
        }
    }

    public Movie updateMovie(Movie movie) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: updateMovie ===");
            System.out.println("Movie ID: " + movie.getMovieId());
            System.out.println("New Title: " + movie.getTitle());
            
            // Check if movie exists
            Movie existingMovie = movieRepository.findById(movie.getMovieId())
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movie.getMovieId()));

            // Validate required fields
            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie title is required");
            }
            if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie genre is required");
            }
            if (movie.getDuration() == null || movie.getDuration() <= 0) {
                throw new IllegalArgumentException("Movie duration must be greater than 0");
            }

            // Update fields
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setGenre(movie.getGenre());
            existingMovie.setDuration(movie.getDuration());
            existingMovie.setLanguage(movie.getLanguage());
            existingMovie.setDirector(movie.getDirector());
            existingMovie.setDescription(movie.getDescription());
            existingMovie.setReleaseYear(movie.getReleaseYear());

            Movie updatedMovie = movieRepository.save(existingMovie);
            System.out.println("Movie updated successfully");
            return updatedMovie;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: updateMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update movie: " + e.getMessage());
        }
    }

    public boolean deleteMovie(Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: deleteMovie ===");
            System.out.println("Movie ID: " + id);
            
            // Check if movie exists
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));

            // Get all showtimes for this movie
            List<ShowTime> showtimes = showTimeRepository.findByMovie(movie);
            System.out.println("Found " + showtimes.size() + " showtimes for movie: " + movie.getTitle());

            // First, delete all bookings associated with these showtimes using native query
            int deletedCount = 0;
            if (!showtimes.isEmpty()) {
                System.out.println("Deleting bookings for movie...");
                deletedCount = bookingRepository.deleteBookingsForMovie(id);
                System.out.println("Successfully deleted " + deletedCount + " bookings");
            }

            // Delete all showtimes for this movie
            for (ShowTime showtime : showtimes) {
                System.out.println("Deleting showtime: " + showtime.getShowtimeId());
                showTimeRepository.delete(showtime);
            }

            // Finally delete the movie
            movieRepository.deleteById(id);
            System.out.println("Movie '" + movie.getTitle() + "' deleted successfully with " + showtimes.size() + " showtimes and " + deletedCount + " bookings");
            return true;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: deleteMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete movie: " + e.getMessage());
        }
    }

    public boolean archiveMovie(Integer id) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: archiveMovie ===");
            System.out.println("Movie ID: " + id);
            
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));

            // For now, we'll just delete the movie (archiving can be implemented later with a status field)
            movieRepository.deleteById(id);
            System.out.println("Movie archived successfully");
            return true;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: archiveMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to archive movie: " + e.getMessage());
        }
    }

    // ========== SHOWTIME MANAGEMENT ==========
    
    public int getShowtimeCountForMovie(Integer movieId) {
        try {
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));
            return showTimeRepository.findByMovie(movie).size();
        } catch (Exception e) {
            System.err.println("Error getting showtime count for movie: " + e.getMessage());
            return 0;
        }
    }

    public List<ShowTime> getShowtimesForMovie(Integer movieId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getShowtimesForMovie ===");
            System.out.println("Movie ID: " + movieId);
            
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

            List<ShowTime> showtimes = showTimeRepository.findByMovie(movie);
            System.out.println("Found " + showtimes.size() + " showtimes for movie: " + movie.getTitle());
            return showtimes;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getShowtimesForMovie ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve showtimes: " + e.getMessage());
        }
    }

    public ShowTime createShowtime(Integer movieId, ShowTime showtime) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: createShowtime ===");
            System.out.println("Movie ID: " + movieId);
            System.out.println("Start Time: " + showtime.getStartTime());
            
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

            // Validate required fields
            if (showtime.getStartTime() == null) {
                throw new IllegalArgumentException("Showtime start time is required");
            }
            if (showtime.getPrice() == null || showtime.getPrice() <= 0) {
                throw new IllegalArgumentException("Showtime price must be greater than 0");
            }
            if (showtime.getCinemaHall() == null) {
                throw new IllegalArgumentException("Cinema hall is required");
            }

            // Calculate end time based on movie duration
            if (showtime.getEndTime() == null) {
                LocalDateTime endTime = showtime.getStartTime().plusMinutes(movie.getDuration());
                showtime.setEndTime(endTime);
            }

            showtime.setMovie(movie);
            ShowTime savedShowtime = showTimeRepository.save(showtime);
            System.out.println("Showtime created successfully with ID: " + savedShowtime.getShowtimeId());
            return savedShowtime;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: createShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create showtime: " + e.getMessage());
        }
    }

    public ShowTime updateShowtime(ShowTime showtime) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: updateShowtime ===");
            System.out.println("Showtime ID: " + showtime.getShowtimeId());
            
            ShowTime existingShowtime = showTimeRepository.findById(showtime.getShowtimeId())
                    .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + showtime.getShowtimeId()));

            // Validate required fields
            if (showtime.getStartTime() == null) {
                throw new IllegalArgumentException("Showtime start time is required");
            }
            if (showtime.getPrice() == null || showtime.getPrice() <= 0) {
                throw new IllegalArgumentException("Showtime price must be greater than 0");
            }

            // Update fields
            existingShowtime.setStartTime(showtime.getStartTime());
            existingShowtime.setEndTime(showtime.getEndTime());
            existingShowtime.setPrice(showtime.getPrice());
            if (showtime.getCinemaHall() != null) {
                existingShowtime.setCinemaHall(showtime.getCinemaHall());
            }

            ShowTime updatedShowtime = showTimeRepository.save(existingShowtime);
            System.out.println("Showtime updated successfully");
            return updatedShowtime;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: updateShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update showtime: " + e.getMessage());
        }
    }

    public boolean deleteShowtime(Integer showtimeId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: deleteShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + showtimeId));

            // Check if showtime has active bookings
            List<Booking> bookings = bookingRepository.findByShowtime(showtime);
            if (!bookings.isEmpty()) {
                throw new IllegalStateException("Cannot delete showtime with active bookings. Please cancel bookings first.");
            }

            showTimeRepository.deleteById(showtimeId);
            System.out.println("Showtime deleted successfully");
            return true;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: deleteShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete showtime: " + e.getMessage());
        }
    }

    // ========== SEAT BOOKING VIEW ==========

    public Map<String, Object> getSeatBookingsForShowtime(Integer showtimeId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getSeatBookingsForShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + showtimeId));

            List<Booking> bookings = bookingRepository.findByShowtime(showtime);
            List<Seat> allSeats = seatRepository.findByCinemaHall(showtime.getCinemaHall());
            
            // Get booked seats
            Set<Seat> bookedSeats = bookings.stream()
                    .flatMap(booking -> booking.getSeats().stream())
                    .collect(Collectors.toSet());

            Map<String, Object> result = new HashMap<>();
            result.put("showtime", showtime);
            result.put("totalSeats", allSeats.size());
            result.put("bookedSeats", bookedSeats.size());
            result.put("availableSeats", allSeats.size() - bookedSeats.size());
            result.put("bookings", bookings);
            result.put("bookedSeatsList", bookedSeats);

            System.out.println("Retrieved booking information: " + bookings.size() + " bookings, " + 
                             bookedSeats.size() + " booked seats out of " + allSeats.size() + " total seats");
            return result;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getSeatBookingsForShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve seat bookings: " + e.getMessage());
        }
    }

    public List<Seat> getSeatStatusForShowtime(Integer showtimeId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getSeatStatusForShowtime ===");
            System.out.println("Showtime ID: " + showtimeId);
            
            ShowTime showtime = showTimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + showtimeId));

            List<Seat> seats = seatRepository.findByCinemaHall(showtime.getCinemaHall());
            List<Booking> bookings = bookingRepository.findByShowtime(showtime);
            
            // Get booked seats
            Set<Seat> bookedSeats = bookings.stream()
                    .flatMap(booking -> booking.getSeats().stream())
                    .collect(Collectors.toSet());

            // Update seat status
            for (Seat seat : seats) {
                if (bookedSeats.contains(seat)) {
                    seat.setStatus("RESERVED");
                } else {
                    seat.setStatus("AVAILABLE");
                }
            }

            System.out.println("Retrieved " + seats.size() + " seats with updated status");
            return seats;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getSeatStatusForShowtime ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve seat status: " + e.getMessage());
        }
    }

    // ========== STATISTICS AND REPORTS ==========

    public Map<String, Object> getMovieStatistics() {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getMovieStatistics ===");
            
            long totalMovies = movieRepository.count();
            long totalShowtimes = showTimeRepository.count();
            long totalBookings = bookingRepository.count();
            
            // Get movies with most showtimes
            List<Object[]> movieShowtimeCounts = showTimeRepository.countShowtimesByMovie();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalMovies", totalMovies);
            statistics.put("totalShowtimes", totalShowtimes);
            statistics.put("totalBookings", totalBookings);
            statistics.put("movieShowtimeCounts", movieShowtimeCounts);

            System.out.println("Retrieved statistics: " + totalMovies + " movies, " + 
                             totalShowtimes + " showtimes, " + totalBookings + " bookings");
            return statistics;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getMovieStatistics ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve statistics: " + e.getMessage());
        }
    }

    public Map<String, Object> getMovieStatistics(Integer movieId) {
        try {
            System.out.println("=== MOVIE MANAGEMENT SERVICE: getMovieStatistics ===");
            System.out.println("Movie ID: " + movieId);
            
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

            List<ShowTime> showtimes = showTimeRepository.findByMovie(movie);
            List<Booking> bookings = showtimes.stream()
                    .flatMap(showtime -> bookingRepository.findByShowtime(showtime).stream())
                    .collect(Collectors.toList());

            int totalSeatsBooked = bookings.stream()
                    .mapToInt(booking -> booking.getTotalSeats())
                    .sum();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("movie", movie);
            statistics.put("totalShowtimes", showtimes.size());
            statistics.put("totalBookings", bookings.size());
            statistics.put("totalSeatsBooked", totalSeatsBooked);
            statistics.put("showtimes", showtimes);

            System.out.println("Retrieved statistics for movie: " + showtimes.size() + " showtimes, " + 
                             bookings.size() + " bookings, " + totalSeatsBooked + " seats booked");
            return statistics;
        } catch (Exception e) {
            System.err.println("=== MOVIE MANAGEMENT SERVICE ERROR: getMovieStatistics ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve movie statistics: " + e.getMessage());
        }
    }
}
