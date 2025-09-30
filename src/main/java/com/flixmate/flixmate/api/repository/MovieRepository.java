package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByIsActiveTrue();
}
