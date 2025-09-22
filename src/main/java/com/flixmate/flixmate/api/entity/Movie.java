package com.flixmate.flixmate.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "genre")
    private String genre;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "language")
    private String language;

    @Column(name = "director")
    private String director;

    @Column(name = "movie_cast")
    private String cast;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "rating")
    private String rating; // PG, PG-13, R, etc.

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;

    @Column(name = "updated_date")
    private java.time.LocalDateTime updatedDate;
    
    // Add JPA lifecycle callbacks for automatic timestamp management
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = java.time.LocalDateTime.now();
        }
        if (updatedDate == null) {
            updatedDate = java.time.LocalDateTime.now();
        }
    }
    
    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedDate = java.time.LocalDateTime.now();
    }

    // Getters, setters, and constructors
    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }
    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public java.time.LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(java.time.LocalDateTime createdDate) { this.createdDate = createdDate; }
    public java.time.LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(java.time.LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public Movie() {}
    public Movie(String title, String description, Integer releaseYear) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
    }

    public Movie(String title, String description, Integer releaseYear, String genre, Integer duration) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.duration = duration;
    }
}



