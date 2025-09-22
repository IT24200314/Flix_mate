package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Review;
import com.flixmate.flixmate.api.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getMovieReviews(@PathVariable Integer movieId) {
        return ResponseEntity.ok(reviewService.getMovieReviews(movieId));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Integer movieId,
                                     @RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.addReview(
                userDetails.getUsername(), 
                movieId, 
                request.getRating(), 
                request.getComment(),
                request.getTitle(),
                request.getIsVerifiedBooking()
            );
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add review: " + e.getMessage());
        }
    }

    // Legacy endpoint for backward compatibility
    @PostMapping("/simple")
    public ResponseEntity<String> addSimpleReview(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Integer movieId,
                                                @RequestParam Integer rating,
                                                @RequestParam(required = false) String comment) {
        try {
            reviewService.addReview(userDetails.getUsername(), movieId, rating, comment);
            return ResponseEntity.ok("Review added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add review: " + e.getMessage());
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Integer movieId,
                                        @PathVariable Integer reviewId,
                                        @RequestBody ReviewRequest request) {
        try {
            boolean updated = reviewService.updateReview(
                userDetails.getUsername(), 
                reviewId, 
                request.getRating(), 
                request.getComment(),
                request.getTitle()
            );
            if (updated) {
                return ResponseEntity.ok("Review updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update review: " + e.getMessage());
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Integer movieId,
                                        @PathVariable Integer reviewId) {
        try {
            boolean deleted = reviewService.deleteReview(userDetails.getUsername(), reviewId);
            if (deleted) {
                return ResponseEntity.ok("Review deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete review: " + e.getMessage());
        }
    }

    @PostMapping("/{reviewId}/report")
    public ResponseEntity<?> reportReview(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Integer movieId,
                                        @PathVariable Integer reviewId,
                                        @RequestParam String reason) {
        try {
            boolean reported = reviewService.reportReview(userDetails.getUsername(), reviewId, reason);
            if (reported) {
                return ResponseEntity.ok("Review reported successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to report review: " + e.getMessage());
        }
    }

    @GetMapping("/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable Integer movieId) {
        try {
            double averageRating = reviewService.getAverageRatingForMovie(movieId);
            return ResponseEntity.ok(Map.of("movieId", movieId, "averageRating", averageRating));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get average rating: " + e.getMessage());
        }
    }

    // Admin endpoints
    @GetMapping("/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReportedReviews() {
        try {
            List<Review> reportedReviews = reviewService.getReportedReviews();
            return ResponseEntity.ok(reportedReviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get reported reviews: " + e.getMessage());
        }
    }

    @PostMapping("/{reviewId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyReview(@PathVariable Integer reviewId) {
        try {
            boolean verified = reviewService.verifyReview(reviewId);
            if (verified) {
                return ResponseEntity.ok("Review verified successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to verify review: " + e.getMessage());
        }
    }

    // Inner class for review request
    public static class ReviewRequest {
        private Integer rating;
        private String comment;
        private String title;
        private Boolean isVerifiedBooking;

        // Getters and setters
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public Boolean getIsVerifiedBooking() { return isVerifiedBooking; }
        public void setIsVerifiedBooking(Boolean isVerifiedBooking) { this.isVerifiedBooking = isVerifiedBooking; }
    }
}
