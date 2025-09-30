package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Staff entity representing cinema staff members
 */
@Entity
@Table(name = "staff")
public class Staff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "position", nullable = false)
    private String position;
    
    @Column(name = "assigned_hall_id")
    private Integer assignedHallId;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "status", nullable = false)
    private String status = "Active";
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Column(name = "salary")
    private java.math.BigDecimal salary;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Note: StaffSchedule uses staffName as String, not a foreign key relationship
    // @OneToMany relationship removed to match the flat table structure
    
    // Constructors
    public Staff() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Staff(String firstName, String lastName, String email, String phone, String position) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.position = position;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Integer getAssignedHallId() {
        return assignedHallId;
    }
    
    public void setAssignedHallId(Integer assignedHallId) {
        this.assignedHallId = assignedHallId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
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
    
    public java.math.BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(java.math.BigDecimal salary) {
        this.salary = salary;
    }
    
    
    // Schedules are managed separately through StaffSchedule entity
    // No direct relationship due to flat table structure
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
