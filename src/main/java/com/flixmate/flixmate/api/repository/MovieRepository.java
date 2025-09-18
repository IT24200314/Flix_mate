package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
