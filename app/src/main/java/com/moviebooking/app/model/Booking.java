package com.moviebooking.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {
    private Long id;
    private String bookingNumber;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long movieId;
    private String movieTitle;
    private Long theaterId;
    private String theaterName;
    private String screenType;
    private LocalDateTime showtime;
    private List<String> seatCodes;
    private BigDecimal totalAmount;
    private String bookingStatus; // "CONFIRMED", "CANCELLED"
    private String paymentStatus;
    private LocalDateTime bookingTime;

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public Long getTheaterId() { return theaterId; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }

    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }

    public String getScreenType() { return screenType; }
    public void setScreenType(String screenType) { this.screenType = screenType; }

    public LocalDateTime getShowtime() { return showtime; }
    public void setShowtime(LocalDateTime showtime) { this.showtime = showtime; }

    public List<String> getSeatCodes() { return seatCodes; }
    public void setSeatCodes(List<String> seatCodes) { this.seatCodes = seatCodes; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(bookingStatus);
    }
}
