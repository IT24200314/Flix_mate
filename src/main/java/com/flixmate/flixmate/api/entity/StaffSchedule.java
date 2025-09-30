package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * StaffSchedule entity representing staff work schedules
 */
@Entity
@Table(name = "staff_schedules")
public class StaffSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;
    
    @Column(name = "staff_name", nullable = false)
    private String staffName;
    
    @Column(name = "start_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    @Column(name = "hall_id", nullable = false)
    private Integer hallId;

    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "shift_type")
    private String shiftType = "Regular";

    @Column(name = "status")
    private String status = "Scheduled";

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructors
    public StaffSchedule() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public StaffSchedule(String staffName, LocalDateTime startTime, LocalDateTime endTime, Integer hallId) {
        this();
        this.staffName = staffName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hallId = hallId;
    }
    
    // Getters and Setters
    public Long getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getHallId() {
        return hallId;
    }
    
    public void setHallId(Integer hallId) {
        this.hallId = hallId;
    }
    
    public Long getStaffId() {
        return staffId;
    }
    
    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
    
    public String getShiftType() {
        return shiftType;
    }
    
    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !startTime.isAfter(now) && !endTime.isBefore(now);
    }
    
    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return startTime.toLocalDate().equals(now.toLocalDate());
    }
    
    public String getHallName() {
        return "Hall " + hallId;
    }
    
    public String getFormattedStartTime() {
        return startTime.toLocalTime().toString();
    }
    
    public String getFormattedEndTime() {
        return endTime.toLocalTime().toString();
    }
    
    public String getFormattedDate() {
        return startTime.toLocalDate().toString();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "StaffSchedule{" +
                "scheduleId=" + scheduleId +
                ", staffName='" + staffName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", hallId=" + hallId +
                '}';
    }
}