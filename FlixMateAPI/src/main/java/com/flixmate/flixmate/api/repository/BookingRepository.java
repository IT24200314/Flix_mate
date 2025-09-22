package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserEmail(String email);
    List<Booking> findByShowtime(ShowTime showtime);
    
    @Modifying
    @Query("UPDATE Booking b SET b.status = 'CANCELLED' WHERE b.showtime.movie.movieId = :movieId")
    int cancelBookingsForMovie(@Param("movieId") Integer movieId);
    
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.showtime.movie.movieId = :movieId")
    int deleteBookingsForMovie(@Param("movieId") Integer movieId);
    
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Booking> findByStatus(String status);
    List<Booking> findByUserEmailAndStatus(String email, String status);
}
