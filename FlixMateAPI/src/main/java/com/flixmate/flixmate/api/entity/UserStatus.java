package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_status")
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status_name", nullable = false, unique = true)
    private String statusName;

    @Column(name = "role", nullable = false)
    private String role; // e.g., "ROLE_USER", "ROLE_ADMIN"

    // Getters, setters, and constructors
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public UserStatus() {}
    public UserStatus(String statusName) {
        this(statusName, "ROLE_USER");
    }
    public UserStatus(String statusName, String role) {
        this.statusName = statusName;
        this.role = role;
    }
}
