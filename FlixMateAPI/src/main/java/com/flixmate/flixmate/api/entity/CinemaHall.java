package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cinema_halls")
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    private Integer hallId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    // Getters, setters, constructors
    public Integer getHallId() { return hallId; }
    public void setHallId(Integer hallId) { this.hallId = hallId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHallName() { return name; }
    public void setHallName(String hallName) { this.name = hallName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public CinemaHall() {}
    public CinemaHall(String name, String location, Integer capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }
}

