package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Integer receiptId;

    @Column(name = "receipt_number", nullable = false, unique = true)
    private String receiptNumber;

    @Column(name = "receipt_date", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime receiptDate;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "showtime_date", nullable = false)
    private String showtimeDate;

    @Column(name = "showtime_time", nullable = false)
    private String showtimeTime;

    @Column(name = "cinema_hall", nullable = false)
    private String cinemaHall;

    @Column(name = "seat_numbers", nullable = false)
    private String seatNumbers;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonIgnore
    private Payment payment;

    // Getters, setters, constructors
    public Integer getReceiptId() { return receiptId; }
    public void setReceiptId(Integer receiptId) { this.receiptId = receiptId; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public LocalDateTime getReceiptDate() { return receiptDate; }
    public void setReceiptDate(LocalDateTime receiptDate) { this.receiptDate = receiptDate; }
    
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    
    public String getShowtimeDate() { return showtimeDate; }
    public void setShowtimeDate(String showtimeDate) { this.showtimeDate = showtimeDate; }
    
    public String getShowtimeTime() { return showtimeTime; }
    public void setShowtimeTime(String showtimeTime) { this.showtimeTime = showtimeTime; }
    
    public String getCinemaHall() { return cinemaHall; }
    public void setCinemaHall(String cinemaHall) { this.cinemaHall = cinemaHall; }
    
    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }
    
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public Receipt() {}
    
    public Receipt(String receiptNumber, LocalDateTime receiptDate, String movieTitle, 
                   String showtimeDate, String showtimeTime, String cinemaHall, 
                   String seatNumbers, Integer totalSeats, Double totalAmount, 
                   String paymentMethod, String transactionId, String userName, 
                   String userEmail, Booking booking, Payment payment) {
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.movieTitle = movieTitle;
        this.showtimeDate = showtimeDate;
        this.showtimeTime = showtimeTime;
        this.cinemaHall = cinemaHall;
        this.seatNumbers = seatNumbers;
        this.totalSeats = totalSeats;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.booking = booking;
        this.payment = payment;
    }
}
