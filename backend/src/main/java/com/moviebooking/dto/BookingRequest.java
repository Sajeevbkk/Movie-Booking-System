package com.moviebooking.dto;

import java.util.List;

public class BookingRequest {
    private Long showtimeId;
    private List<Long> seatIds;

    public BookingRequest() {}

    public BookingRequest(Long showtimeId, List<Long> seatIds) {
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
    }

    public Long getShowtimeId() { return showtimeId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }

    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
}
