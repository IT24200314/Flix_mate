package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "type", nullable = false)
    private String type; // e.g., "REVENUE", "POPULARITY", "TICKET_SALES"

    @Column(name = "data", nullable = false)
    private String data; // JSON or text representation of report

    @Column(name = "generated_date", nullable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime generatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Admin who generated the report

    // Getters, setters, constructors
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Report() {}
    public Report(String type, String data, LocalDateTime generatedDate, User user) {
        this.type = type;
        this.data = data;
        this.generatedDate = generatedDate;
        this.user = user;
    }
}
