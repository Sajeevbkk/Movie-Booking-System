package com.moviebooking.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Seat {
    private Long id;
    private Long theaterId;
    private String seatRow;
    private Integer seatNumber;
    private String seatCode;
    private String seatType; // "REGULAR", "VIP"
    @JsonProperty("isBooked")
    @JsonAlias({"booked", "isBooked"})
    private boolean isBooked;
    private BigDecimal price;

    public Seat() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTheaterId() { return theaterId; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }

    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }

    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isVip() {
        return "VIP".equalsIgnoreCase(seatType);
    }
}
