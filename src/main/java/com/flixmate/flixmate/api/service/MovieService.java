package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        try {
            System.out.println("=== MOVIE SERVICE: getAllMovies ===");
            List<Movie> movies = movieRepository.findAll();
            System.out.println("Found " + movies.size() + " movies");
            
            // Log each movie for debugging
            for (Movie movie : movies) {
                System.out.println("Movie - ID: " + movie.getMovieId() + 
                                 ", Title: " + movie.getTitle() + 
                                 ", Genre: " + movie.getGenre() + 
                                 ", Year: " + movie.getReleaseYear());
            }
            
            return movies;
        } catch (Exception e) {
            System.err.println("=== MOVIE SERVICE ERROR: getAllMovies ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get all movies: " + e.getMessage());
        }
    }

    public Movie getMovieById(Integer id) {
        try {
            System.out.println("=== MOVIE SERVICE: getMovieById ===");
            System.out.println("Movie ID: " + id);
            
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid movie ID: " + id);
            }
            
            Movie movie = movieRepository.findById(id).orElse(null);
            if (movie != null) {
                System.out.println("Found movie: " + movie.getTitle() + " (ID: " + movie.getMovieId() + ")");
            } else {
                System.out.println("Movie not found with ID: " + id);
            }
            
            return movie;
        } catch (IllegalArgumentException e) {
            System.err.println("=== MOVIE SERVICE VALIDATION ERROR: getMovieById ===");
            System.err.println("Error Message: " + e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            System.err.println("=== MOVIE SERVICE ERROR: getMovieById ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Movie ID: " + id);
            e.printStackTrace();
            throw new RuntimeException("Failed to get movie by ID: " + e.getMessage());
        }
    }

    public Movie addMovie(Movie movie) {
        try {
            System.out.println("=== MOVIE SERVICE: addMovie ===");
            System.out.println("Movie Title: " + (movie != null ? movie.getTitle() : "NULL"));
            
            if (movie == null) {
                throw new IllegalArgumentException("Movie cannot be null");
            }
            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie title cannot be null or empty");
            }
            if (movie.getReleaseYear() != null && (movie.getReleaseYear() < 1900 || movie.getReleaseYear() > 2030)) {
                throw new IllegalArgumentException("Invalid release year: " + movie.getReleaseYear());
            }
            if (movie.getDuration() != null && (movie.getDuration() <= 0 || movie.getDuration() > 600)) {
                throw new IllegalArgumentException("Invalid duration: " + movie.getDuration() + " minutes");
            }
            
            Movie savedMovie = movieRepository.save(movie);
            System.out.println("Movie saved successfully with ID: " + savedMovie.getMovieId());
            return savedMovie;
        } catch (IllegalArgumentException e) {
            System.err.println("=== MOVIE SERVICE VALIDATION ERROR: addMovie ===");
            System.err.println("Error Message: " + e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            System.err.println("=== MOVIE SERVICE ERROR: addMovie ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add movie: " + e.getMessage());
        }
    }

    public boolean updateMovie(Integer id, Movie movie) {
        try {
            System.out.println("=== MOVIE SERVICE: updateMovie ===");
            System.out.println("Movie ID: " + id);
            System.out.println("Movie Title: " + (movie != null ? movie.getTitle() : "NULL"));
            
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid movie ID: " + id);
            }
            if (movie == null) {
                throw new IllegalArgumentException("Movie cannot be null");
            }
            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie title cannot be null or empty");
            }
            if (movie.getReleaseYear() != null && (movie.getReleaseYear() < 1900 || movie.getReleaseYear() > 2030)) {
                throw new IllegalArgumentException("Invalid release year: " + movie.getReleaseYear());
            }
            if (movie.getDuration() != null && (movie.getDuration() <= 0 || movie.getDuration() > 600)) {
                throw new IllegalArgumentException("Invalid duration: " + movie.getDuration() + " minutes");
            }
            
            if (movieRepository.existsById(id)) {
                movie.setMovieId(id);
                movieRepository.save(movie);
                System.out.println("Movie updated successfully with ID: " + id);
                return true;
            } else {
                System.out.println("Movie not found with ID: " + id);
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("=== MOVIE SERVICE VALIDATION ERROR: updateMovie ===");
            System.err.println("Error Message: " + e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            System.err.println("=== MOVIE SERVICE ERROR: updateMovie ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Movie ID: " + id);
            e.printStackTrace();
            throw new RuntimeException("Failed to update movie: " + e.getMessage());
        }
    }

    public boolean deleteMovie(Integer id) {
        try {
            System.out.println("=== MOVIE SERVICE: deleteMovie ===");
            System.out.println("Movie ID: " + id);
            
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid movie ID: " + id);
            }
            
            if (movieRepository.existsById(id)) {
                movieRepository.deleteById(id);
                System.out.println("Movie deleted successfully with ID: " + id);
                return true;
            } else {
                System.out.println("Movie not found with ID: " + id);
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("=== MOVIE SERVICE VALIDATION ERROR: deleteMovie ===");
            System.err.println("Error Message: " + e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            System.err.println("=== MOVIE SERVICE ERROR: deleteMovie ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Movie ID: " + id);
            e.printStackTrace();
            throw new RuntimeException("Failed to delete movie: " + e.getMessage());
        }
    }
}
