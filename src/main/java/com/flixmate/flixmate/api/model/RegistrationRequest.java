package com.flixmate.flixmate.api.model;

public class RegistrationRequest {
    private String email;
    private String password;
    private String userName;
    private String statusName; // To link with user_status

    // Getters, setters, and no-args constructor
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public RegistrationRequest() {}
}
