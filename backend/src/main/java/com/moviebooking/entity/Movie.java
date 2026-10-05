package com.moviebooking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 80)
    private String genre;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(length = 50)
    private String language;

    @Column(length = 20)
    private String rating; // e.g. "PG-13", "8.8/10"

    private LocalDate releaseDate;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] posterData;

    @Column(length = 50)
    private String posterContentType; // e.g. "image/jpeg", "image/png"

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Movie() {}

    public Movie(String title, String description, String genre, Integer durationMinutes,
                 String language, String rating, LocalDate releaseDate, byte[] posterData,
                 String posterContentType, Boolean isActive) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterData = posterData;
        this.posterContentType = posterContentType;
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public byte[] getPosterData() { return posterData; }
    public void setPosterData(byte[] posterData) { this.posterData = posterData; }

    public String getPosterContentType() { return posterContentType; }
    public void setPosterContentType(String posterContentType) { this.posterContentType = posterContentType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
