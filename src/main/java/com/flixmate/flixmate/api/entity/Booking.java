package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_user_status", columnList = "user_id, status")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "booking_date", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime bookingDate;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "CONFIRMED", "CANCELLED"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "showtime_id", nullable = false)
    private ShowTime showtime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "booking_seats",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> seats;

    // Getters, setters, constructors
    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ShowTime getShowtime() { return showtime; }
    public void setShowtime(ShowTime showtime) { this.showtime = showtime; }
    public Set<Seat> getSeats() { return seats; }
    public void setSeats(Set<Seat> seats) { this.seats = seats; }

    public Booking() {}
    public Booking(LocalDateTime bookingDate, Integer totalSeats, Double totalAmount, String status, User user, ShowTime showtime, Set<Seat> seats) {
        this.bookingDate = bookingDate;
        this.totalSeats = totalSeats;
        this.totalAmount = totalAmount;
        this.status = status;
        this.user = user;
        this.showtime = showtime;
        this.seats = seats;
    }
}
