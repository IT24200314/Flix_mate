package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.entity.Review;
import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.repository.MovieRepository;
import com.flixmate.flixmate.api.repository.ReviewRepository;
import com.flixmate.flixmate.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    public List<Review> getMovieReviews(Integer movieId) {
        return reviewRepository.findByMovie_MovieId(movieId);
    }

    public Review addReview(String email, Integer movieId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Review review = new Review(rating, comment, LocalDateTime.now(), user, movie);
        return reviewRepository.save(review);
    }

    public boolean updateReview(String email, Integer reviewId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if the review belongs to the user
        if (!review.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only update your own reviews");
        }

        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);
        return true;
    }

    public boolean deleteReview(String email, Integer reviewId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if the review belongs to the user
        if (!review.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        reviewRepository.deleteById(reviewId);
        return true;
    }
}

