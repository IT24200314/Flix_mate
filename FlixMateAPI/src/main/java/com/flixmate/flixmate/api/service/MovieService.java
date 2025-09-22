package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Integer id) {
        return movieRepository.findById(id).orElse(null);
    }

    public Movie addMovie(Movie movie) {
        // Set timestamps for new movie
        movie.setCreatedDate(LocalDateTime.now());
        movie.setUpdatedDate(LocalDateTime.now());
        if (movie.getIsActive() == null) {
            movie.setIsActive(true);
        }
        System.out.println("Adding new movie: " + movie.getTitle());
        Movie savedMovie = movieRepository.save(movie);
        System.out.println("Movie saved with ID: " + savedMovie.getMovieId());
        return savedMovie;
    }

    public boolean updateMovie(Integer id, Movie movie) {
        if (movieRepository.existsById(id)) {
            // Get the existing movie to preserve the original created_date
            Movie existingMovie = movieRepository.findById(id).orElse(null);
            if (existingMovie != null) {
                // Preserve the original created_date
                movie.setCreatedDate(existingMovie.getCreatedDate());
                movie.setMovieId(id);
                movie.setUpdatedDate(LocalDateTime.now());
                System.out.println("Updating movie ID: " + id + ", Title: " + movie.getTitle());
                movieRepository.save(movie);
                System.out.println("Movie updated successfully");
                return true;
            }
        }
        System.out.println("Movie not found for update, ID: " + id);
        return false;
    }

    public boolean deleteMovie(Integer id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
