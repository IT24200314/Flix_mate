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
        return reviewRepository.findByMovieIdWithUserAndMovie(movieId);
    }

    public Review addReview(String email, Integer movieId, Integer rating, String comment) {
        return addReview(email, movieId, rating, comment, null, false);
    }

    public Review addReview(String email, Integer movieId, Integer rating, String comment, String title, Boolean isVerifiedBooking) {
        try {
            if (rating < 1 || rating > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            // Check if user already reviewed this movie
            List<Review> existingReviews = reviewRepository.findByUserAndMovie(user, movie);
            if (!existingReviews.isEmpty()) {
                throw new RuntimeException("You have already reviewed this movie");
            }

            Review review = new Review(rating, comment, LocalDateTime.now(), user, movie);
            review.setTitle(title);
            review.setIsVerifiedBooking(isVerifiedBooking != null ? isVerifiedBooking : false);
            
            return reviewRepository.save(review);
        } catch (Exception e) {
            System.err.println("Error adding review: " + e.getMessage());
            throw new RuntimeException("Failed to add review: " + e.getMessage());
        }
    }

    public boolean updateReview(String email, Integer reviewId, Integer rating, String comment) {
        return updateReview(email, reviewId, rating, comment, null);
    }

    public boolean updateReview(String email, Integer reviewId, Integer rating, String comment, String title) {
        try {
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
            if (title != null) {
                review.setTitle(title);
            }
            reviewRepository.save(review);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating review: " + e.getMessage());
            throw new RuntimeException("Failed to update review: " + e.getMessage());
        }
    }

    public boolean deleteReview(String email, Integer reviewId) {
        try {
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
        } catch (Exception e) {
            System.err.println("Error deleting review: " + e.getMessage());
            throw new RuntimeException("Failed to delete review: " + e.getMessage());
        }
    }

    public boolean reportReview(String email, Integer reviewId, String reason) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            // Check if user is not reporting their own review
            if (review.getUser().getEmail().equals(email)) {
                throw new RuntimeException("You cannot report your own review");
            }

            review.setIsReported(true);
            review.setReportReason(reason);
            reviewRepository.save(review);
            return true;
        } catch (Exception e) {
            System.err.println("Error reporting review: " + e.getMessage());
            throw new RuntimeException("Failed to report review: " + e.getMessage());
        }
    }

    public boolean verifyReview(Integer reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            review.setIsVerifiedBooking(true);
            reviewRepository.save(review);
            return true;
        } catch (Exception e) {
            System.err.println("Error verifying review: " + e.getMessage());
            throw new RuntimeException("Failed to verify review: " + e.getMessage());
        }
    }

    public List<Review> getReportedReviews() {
        try {
            return reviewRepository.findReportedReviewsWithUserAndMovie();
        } catch (Exception e) {
            System.err.println("Error fetching reported reviews: " + e.getMessage());
            return List.of();
        }
    }

    public double getAverageRatingForMovie(Integer movieId) {
        try {
            List<Review> reviews = reviewRepository.findByMovieIdWithUserAndMovie(movieId);
            if (reviews.isEmpty()) {
                return 0.0;
            }
            return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        } catch (Exception e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
            return 0.0;
        }
    }
}

