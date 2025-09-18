package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Review;
import com.flixmate.flixmate.api.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> addReview(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Integer movieId,
                                          @RequestParam Integer rating,
                                          @RequestParam(required = false) String comment) {
        reviewService.addReview(userDetails.getUsername(), movieId, rating, comment);
        return ResponseEntity.ok("Review added successfully");
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Integer movieId,
                                            @PathVariable Integer reviewId,
                                            @RequestParam Integer rating,
                                            @RequestParam(required = false) String comment) {
        boolean updated = reviewService.updateReview(userDetails.getUsername(), reviewId, rating, comment);
        if (updated) {
            return ResponseEntity.ok("Review updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Integer movieId,
                                            @PathVariable Integer reviewId) {
        boolean deleted = reviewService.deleteReview(userDetails.getUsername(), reviewId);
        if (deleted) {
            return ResponseEntity.ok("Review deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
