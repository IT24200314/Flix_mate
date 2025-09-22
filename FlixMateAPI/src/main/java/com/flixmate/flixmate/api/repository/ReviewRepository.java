package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Review;
import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByMovie_MovieId(Integer movieId);
    List<Review> findByUserAndMovie(User user, Movie movie);
    List<Review> findByIsReportedTrue();
    List<Review> findByIsVerifiedBookingTrue();
    
    @Query("SELECT r FROM Review r JOIN FETCH r.user u JOIN FETCH r.movie m WHERE r.movie.movieId = :movieId")
    List<Review> findByMovieIdWithUserAndMovie(@Param("movieId") Integer movieId);
    
    @Query("SELECT r FROM Review r JOIN FETCH r.user u JOIN FETCH r.movie m WHERE r.isReported = true")
    List<Review> findReportedReviewsWithUserAndMovie();
}

