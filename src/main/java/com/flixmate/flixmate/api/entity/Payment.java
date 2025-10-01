package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "payment_date", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL"

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "SUCCESS", "FAILED", "PENDING", "REFUNDED"

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "refund_date")
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime refundDate;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Getters, setters, constructors
    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
    public LocalDateTime getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDateTime refundDate) { this.refundDate = refundDate; }
    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Payment() {}
    public Payment(String transactionId, LocalDateTime paymentDate, String paymentMethod, Double amount, String status, Booking booking) {
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.booking = booking;
    }
}
