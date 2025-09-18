package com.flixmate.flixmate.api.model;

public class ProfileDTO {
    private String email;
    private String userName;
    private String phone;
    private String statusName;

    // Getters, setters, and no-args constructor
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public ProfileDTO() {}
    public ProfileDTO(String email, String userName, String phone, String statusName) {
        this.email = email;
        this.userName = userName;
        this.phone = phone;
        this.statusName = statusName;
    }
}
