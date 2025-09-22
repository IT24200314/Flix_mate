package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShowTimeRepository extends JpaRepository<ShowTime, Integer> {
    List<ShowTime> findByMovie_MovieId(Integer movieId);
    List<ShowTime> findByMovie(Movie movie);
    
    @Query("SELECT s.movie, COUNT(s) FROM ShowTime s GROUP BY s.movie")
    List<Object[]> countShowtimesByMovie();
}
