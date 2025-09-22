package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;
import com.flixmate.flixmate.api.util.LocalDateTimeStringAttributeConverter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "registration_date", nullable = false, updatable = false)
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime registrationDate = LocalDateTime.now();
    
    @Column(name = "last_login")
    @Convert(converter = LocalDateTimeStringAttributeConverter.class)
    private LocalDateTime lastLogin;
    
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private UserStatus status;

    // Constructors
    public User() {}
    
    public User(String userName, String passwordHash, String email, String phone) {
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
    }
    
    public User(String email, String passwordHash, String userName, UserStatus status) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.userName = userName;
        this.status = status;
        this.registrationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getUserId() { 
        return userId; 
    }
    
    public void setUserId(Integer userId) { 
        this.userId = userId; 
    }
    
    public String getUserName() { 
        return userName; 
    }
    
    public void setUserName(String userName) { 
        this.userName = userName; 
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
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
    
    public LocalDateTime getRegistrationDate() { 
        return registrationDate; 
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) { 
        this.registrationDate = registrationDate; 
    }
    
    public LocalDateTime getLastLogin() { 
        return lastLogin; 
    }
    
    public void setLastLogin(LocalDateTime lastLogin) { 
        this.lastLogin = lastLogin; 
    }
    
    public UserStatus getStatus() { 
        return status; 
    }
    
    public void setStatus(UserStatus status) { 
        this.status = status; 
    }
}





