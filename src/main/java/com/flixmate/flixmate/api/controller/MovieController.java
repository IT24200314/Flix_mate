package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<?> getAllMovies() {
        System.out.println("=== MOVIES API CALL START ===");
        
        try {
            System.out.println("Fetching all movies");
            List<Movie> movies = movieService.getAllMovies();
            System.out.println("Found " + movies.size() + " movies");
            
            // Log each movie details
            for (Movie movie : movies) {
                System.out.println("Movie ID: " + movie.getMovieId() + 
                                 ", Title: " + movie.getTitle() + 
                                 ", Genre: " + movie.getGenre() + 
                                 ", Year: " + movie.getReleaseYear());
            }
            
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("=== MOVIES API ERROR ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END MOVIES API ERROR ===");
            
            return ResponseEntity.badRequest().body("Failed to fetch movies: " + e.getMessage());
        } finally {
            System.out.println("=== MOVIES API CALL END ===");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Integer id) {
        Movie movie = movieService.getMovieById(id);
        if (movie != null) {
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        try {
            movieService.addMovie(movie);
            return ResponseEntity.ok("Movie added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add movie: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateMovie(@PathVariable Integer id, @RequestBody Movie movie) {
        boolean updated = movieService.updateMovie(id, movie);
        if (updated) {
            return ResponseEntity.ok("Movie updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMovie(@PathVariable Integer id) {
        boolean deleted = movieService.deleteMovie(id);
        if (deleted) {
            return ResponseEntity.ok("Movie deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
