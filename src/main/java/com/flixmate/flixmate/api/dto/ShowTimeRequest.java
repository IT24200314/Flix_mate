package com.flixmate.flixmate.api.dto;

public class ShowTimeRequest {
    private Integer movieId;
    private Integer hallId;
    private String startTime;
    private String endTime;
    private Double price;

    // Default constructor
    public ShowTimeRequest() {}

    // Constructor with all fields
    public ShowTimeRequest(Integer movieId, Integer hallId, String startTime, String endTime, Double price) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    // Getters and setters
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getHallId() {
        return hallId;
    }

    public void setHallId(Integer hallId) {
        this.hallId = hallId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ShowTimeRequest{" +
                "movieId=" + movieId +
                ", hallId=" + hallId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", price=" + price +
                '}';
    }
}
