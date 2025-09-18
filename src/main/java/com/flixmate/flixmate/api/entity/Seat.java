package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "seats", indexes = {
    @Index(name = "idx_hall_status", columnList = "hall_id, status")
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Integer seatId;

    @Column(name = "\"row\"", nullable = false)
    private String row;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "AVAILABLE", "RESERVED"

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall cinemaHall;

    // Getters, setters, constructors
    public Integer getSeatId() { return seatId; }
    public void setSeatId(Integer seatId) { this.seatId = seatId; }
    public String getRow() { return row; }
    public void setRow(String row) { this.row = row; }
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public CinemaHall getCinemaHall() { return cinemaHall; }
    public void setCinemaHall(CinemaHall cinemaHall) { this.cinemaHall = cinemaHall; }

    public Seat() {}
    public Seat(String row, Integer number, String status, CinemaHall cinemaHall) {
        this.row = row;
        this.number = number;
        this.status = status;
        this.cinemaHall = cinemaHall;
    }
}

