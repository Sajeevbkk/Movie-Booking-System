package com.moviebooking.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Showtime {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theaterId;
    private String theaterName;
    private String theaterCity;
    private String screenType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal regularPrice;
    private BigDecimal vipPrice;
    private String status;

    public Showtime() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public Long getTheaterId() { return theaterId; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }

    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }

    public String getTheaterCity() { return theaterCity; }
    public void setTheaterCity(String theaterCity) { this.theaterCity = theaterCity; }

    public String getScreenType() { return screenType; }
    public void setScreenType(String screenType) { this.screenType = screenType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public BigDecimal getRegularPrice() { return regularPrice; }
    public void setRegularPrice(BigDecimal regularPrice) { this.regularPrice = regularPrice; }

    public BigDecimal getVipPrice() { return vipPrice; }
    public void setVipPrice(BigDecimal vipPrice) { this.vipPrice = vipPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
