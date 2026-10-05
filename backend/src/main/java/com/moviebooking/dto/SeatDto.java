package com.moviebooking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moviebooking.entity.enums.SeatType;
import java.math.BigDecimal;

public class SeatDto {
    private Long id;
    private Long theaterId;
    private String seatRow;
    private Integer seatNumber;
    private String seatCode;
    private SeatType seatType;
    @JsonProperty("isBooked")
    @JsonAlias({"booked", "isBooked"})
    private boolean isBooked;
    private BigDecimal price;

    public SeatDto() {}

    public SeatDto(Long id, Long theaterId, String seatRow, Integer seatNumber, String seatCode,
                   SeatType seatType, boolean isBooked, BigDecimal price) {
        this.id = id;
        this.theaterId = theaterId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.isBooked = isBooked;
        this.price = price;
    }

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

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }

    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
