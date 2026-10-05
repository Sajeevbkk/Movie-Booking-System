package com.moviebooking.dto;

import java.time.LocalDate;

public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private Integer durationMinutes;
    private String language;
    private String rating;
    private LocalDate releaseDate;
    private Boolean hasPoster;
    private String posterUrl;
    private Boolean isActive;

    public MovieDto() {}

    public MovieDto(Long id, String title, String description, String genre, Integer durationMinutes,
                    String language, String rating, LocalDate releaseDate, Boolean hasPoster,
                    String posterUrl, Boolean isActive) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.hasPoster = hasPoster;
        this.posterUrl = posterUrl;
        this.isActive = isActive;
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

    public Boolean getHasPoster() { return hasPoster; }
    public void setHasPoster(Boolean hasPoster) { this.hasPoster = hasPoster; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
