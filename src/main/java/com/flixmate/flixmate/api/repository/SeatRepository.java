package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Seat;
import com.flixmate.flixmate.api.entity.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByCinemaHall_HallIdAndStatus(Integer hallId, String status);
    List<Seat> findByCinemaHall(CinemaHall cinemaHall);
}

