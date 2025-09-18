package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByMovie_MovieId(Integer movieId);
}

