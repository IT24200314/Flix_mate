package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;
import java.time.LocalDateTime;


@Entity
@Table(name = "showtimes")
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Integer showtimeId;

    @Column(name = "start_time", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime endTime;

    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall cinemaHall;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Getters, setters, constructors
    public Integer getShowtimeId() { return showtimeId; }
    public void setShowtimeId(Integer showtimeId) { this.showtimeId = showtimeId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public CinemaHall getCinemaHall() { return cinemaHall; }
    public void setCinemaHall(CinemaHall cinemaHall) { this.cinemaHall = cinemaHall; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public ShowTime() {}
    public ShowTime(LocalDateTime startTime, LocalDateTime endTime, Double price, CinemaHall cinemaHall, Movie movie) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.cinemaHall = cinemaHall;
        this.movie = movie;
    }
}





