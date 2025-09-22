package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> checkDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection by counting movies
            long movieCount = movieRepository.count();
            response.put("status", "success");
            response.put("message", "Database connection successful");
            response.put("movieCount", movieCount);
            response.put("timestamp", System.currentTimeMillis());
            
            // Get sample movie data
            if (movieCount > 0) {
                Movie sampleMovie = movieRepository.findAll().get(0);
                Map<String, Object> sampleData = new HashMap<>();
                sampleData.put("id", sampleMovie.getMovieId());
                sampleData.put("title", sampleMovie.getTitle());
                sampleData.put("isActive", sampleMovie.getIsActive());
                response.put("sampleMovie", sampleData);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/movies")
    public ResponseEntity<Map<String, Object>> checkMovies() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all active movies
            var movies = movieRepository.findByIsActiveTrue();
            response.put("status", "success");
            response.put("message", "Movies retrieved successfully");
            response.put("count", movies.size());
            response.put("movies", movies);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to retrieve movies: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
