package com.moviebooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShowtimeCreateRequest {
    private Long movieId;
    private Long theaterId;
    private LocalDateTime startTime;
    private BigDecimal regularPrice;
    private BigDecimal vipPrice;

    public ShowtimeCreateRequest() {}

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getTheaterId() { return theaterId; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public BigDecimal getRegularPrice() { return regularPrice; }
    public void setRegularPrice(BigDecimal regularPrice) { this.regularPrice = regularPrice; }

    public BigDecimal getVipPrice() { return vipPrice; }
    public void setVipPrice(BigDecimal vipPrice) { this.vipPrice = vipPrice; }
}
