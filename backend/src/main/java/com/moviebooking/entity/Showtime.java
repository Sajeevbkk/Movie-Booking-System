package com.moviebooking.entity;

import com.moviebooking.entity.enums.ShowtimeStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal regularPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vipPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShowtimeStatus status = ShowtimeStatus.ACTIVE;

    public Showtime() {}

    public Showtime(Movie movie, Theater theater, LocalDateTime startTime, LocalDateTime endTime,
                    BigDecimal regularPrice, BigDecimal vipPrice, ShowtimeStatus status) {
        this.movie = movie;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
        this.regularPrice = regularPrice;
        this.vipPrice = vipPrice;
        this.status = status != null ? status : ShowtimeStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Theater getTheater() { return theater; }
    public void setTheater(Theater theater) { this.theater = theater; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public BigDecimal getRegularPrice() { return regularPrice; }
    public void setRegularPrice(BigDecimal regularPrice) { this.regularPrice = regularPrice; }

    public BigDecimal getVipPrice() { return vipPrice; }
    public void setVipPrice(BigDecimal vipPrice) { this.vipPrice = vipPrice; }

    public ShowtimeStatus getStatus() { return status; }
    public void setStatus(ShowtimeStatus status) { this.status = status; }
}
