package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.PromotionalBanner;
import com.flixmate.flixmate.api.service.MovieService;
import com.flixmate.flixmate.api.service.ShowTimeService;
import com.flixmate.flixmate.api.service.PromotionalBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicMovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowTimeService showTimeService;

    @Autowired
    private PromotionalBannerService bannerService;

    @GetMapping("/movies")
    public ResponseEntity<?> getAllMovies() {
        System.out.println("=== PUBLIC MOVIES API CALL START ===");
        
        try {
            System.out.println("Fetching all movies from service...");
            List<Movie> movies = movieService.getAllMovies();
            System.out.println("Total movies from service: " + movies.size());
            
            // Log each movie details and set poster URLs if missing
            for (Movie movie : movies) {
                System.out.println("Movie ID: " + movie.getMovieId() + 
                                 ", Title: " + movie.getTitle() + 
                                 ", Genre: " + movie.getGenre() + 
                                 ", Year: " + movie.getReleaseYear() +
                                 ", Active: " + movie.getIsActive() +
                                 ", Poster: " + movie.getPosterUrl());
                
                // Auto-assign poster URLs if missing
                if (movie.getPosterUrl() == null || movie.getPosterUrl().isEmpty()) {
                    movie.setPosterUrl(generatePosterUrl(movie.getMovieId()));
                }
            }
            
            // Filter out inactive movies for public access
            List<Movie> activeMovies = movies.stream()
                    .filter(movie -> movie.getIsActive() == null || movie.getIsActive())
                    .toList();
            
            System.out.println("Active movies after filtering: " + activeMovies.size());
            
            return ResponseEntity.ok(activeMovies);
        } catch (Exception e) {
            System.err.println("=== PUBLIC MOVIES API ERROR ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END PUBLIC MOVIES API ERROR ===");
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to fetch movies",
                "message", e.getMessage(),
                "type", e.getClass().getSimpleName(),
                "timestamp", System.currentTimeMillis()
            ));
        } finally {
            System.out.println("=== PUBLIC MOVIES API CALL END ===");
        }
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Integer id) {
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie != null && (movie.getIsActive() == null || movie.getIsActive())) {
                return ResponseEntity.ok(movie);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching movie by ID: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch movie: " + e.getMessage());
        }
    }

    @GetMapping("/movies/search")
    public ResponseEntity<?> searchMovies(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String genre,
                                        @RequestParam(required = false) Integer year) {
        try {
            List<Movie> allMovies = movieService.getAllMovies();
            List<Movie> filteredMovies = allMovies.stream()
                    .filter(movie -> movie.getIsActive() == null || movie.getIsActive())
                    .filter(movie -> title == null || movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .filter(movie -> genre == null || (movie.getGenre() != null && movie.getGenre().toLowerCase().contains(genre.toLowerCase())))
                    .filter(movie -> year == null || movie.getReleaseYear().equals(year))
                    .toList();
            return ResponseEntity.ok(filteredMovies);
        } catch (Exception e) {
            System.err.println("Error searching movies: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to search movies: " + e.getMessage());
        }
    }

    @GetMapping("/movies/{movieId}/showtimes")
    public ResponseEntity<?> getMovieShowtimes(@PathVariable Integer movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            if (movie == null || (movie.getIsActive() != null && !movie.getIsActive())) {
                return ResponseEntity.notFound().build();
            }

            List<ShowTime> showtimes = showTimeService.getShowTimesByMovie(movieId);
            return ResponseEntity.ok(showtimes);
        } catch (Exception e) {
            System.err.println("Error fetching movie showtimes: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch showtimes: " + e.getMessage());
        }
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<?> getShowtimeSeatAvailability(@PathVariable Integer showtimeId) {
        try {
            // This endpoint allows public access to see seat availability
            // but doesn't allow actual booking without authentication
            ShowTime showtime = showTimeService.getShowTimeById(showtimeId);
            if (showtime == null) {
                return ResponseEntity.notFound().build();
            }

            // Return basic seat availability info (without seat details)
            Map<String, Object> seatInfo = Map.of(
                "showtimeId", showtimeId,
                "totalSeats", 100, // Mock total seats
                "availableSeats", 85, // Mock available seats
                "occupiedSeats", 15 // Mock occupied seats
            );
            return ResponseEntity.ok(seatInfo);
        } catch (Exception e) {
            System.err.println("Error fetching seat availability: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch seat availability: " + e.getMessage());
        }
    }

    @GetMapping("/banners")
    public ResponseEntity<?> getPromotionalBanners() {
        try {
            List<PromotionalBanner> banners = bannerService.getActiveBanners();
            return ResponseEntity.ok(banners);
        } catch (Exception e) {
            System.err.println("Error fetching promotional banners: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch banners: " + e.getMessage());
        }
    }

    @GetMapping("/banners/{id}/click")
    public ResponseEntity<?> trackBannerClick(@PathVariable Integer id) {
        try {
            bannerService.incrementClickCount(id);
            return ResponseEntity.ok("Click tracked successfully");
        } catch (Exception e) {
            System.err.println("Error tracking banner click: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to track click: " + e.getMessage());
        }
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getAvailableGenres() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            List<String> genres = movies.stream()
                    .filter(movie -> movie.getIsActive() == null || movie.getIsActive())
                    .map(Movie::getGenre)
                    .filter(genre -> genre != null && !genre.trim().isEmpty())
                    .distinct()
                    .sorted()
                    .toList();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            System.err.println("Error fetching genres: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch genres: " + e.getMessage());
        }
    }

    @GetMapping("/years")
    public ResponseEntity<?> getAvailableYears() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            List<Integer> years = movies.stream()
                    .filter(movie -> movie.getIsActive() == null || movie.getIsActive())
                    .map(Movie::getReleaseYear)
                    .filter(year -> year != null)
                    .distinct()
                    .sorted((a, b) -> b.compareTo(a)) // Sort descending
                    .toList();
            return ResponseEntity.ok(years);
        } catch (Exception e) {
            System.err.println("Error fetching years: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch years: " + e.getMessage());
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedMovies() {
        try {
            List<Movie> allMovies = movieService.getAllMovies();
            List<Movie> featuredMovies = allMovies.stream()
                    .filter(movie -> movie.getIsActive() == null || movie.getIsActive())
                    .limit(6) // Return top 6 movies as featured
                    .toList();
            return ResponseEntity.ok(featuredMovies);
        } catch (Exception e) {
            System.err.println("Error fetching featured movies: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch featured movies: " + e.getMessage());
        }
    }

    // Helper method to generate poster URLs
    private String generatePosterUrl(Integer movieId) {
        if (movieId == null) return null;
        
        String[] availablePosters = {
            "movie_12bcc189-4bcf-4780-aaa4-1c5360b1e8f1.png",
            "movie_1ea930ff-f2e9-42d6-b560-5eba79dc4112.png",
            "movie_244ff64f-00d4-4c7d-91f8-6a186603ff41.png",
            "movie_9c136ae0-1345-4de0-8205-3c9d7a74ccae.png",
            "movie_9c3b2a8e-de38-4374-8608-41ccdacef4ac.png",
            "movie_a3c0501b-7516-4be0-8804-0b9ff66cbbf9.png",
            "movie_ad41b530-1a80-4db6-91af-c318e8a3b2d3.png",
            "movie_b1b55829-8f62-4e42-8e97-5e7b4209835c.png",
            "movie_b92cb7c6-bd0c-4e39-8a48-7d805c0944ac.png",
            "movie_bc4bbf60-c9d4-46c5-a95b-7ce8c44ed68c.png",
            "movie_e2f1ce42-0f35-4e2f-bb6c-fcf57f8c02cd.png"
        };
        
        int posterIndex = movieId % availablePosters.length;
        return "static/images/" + availablePosters[posterIndex];
    }
}
