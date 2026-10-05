package com.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moviebooking.entity.enums.SeatType;
import jakarta.persistence.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"theater_id", "seat_code"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    @JsonIgnore
    private Theater theater;

    @Column(name = "seat_row", nullable = false, length = 5)
    private String seatRow; // "A", "B", etc.

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 1, 2, 3...

    @Column(name = "seat_code", nullable = false, length = 10)
    private String seatCode; // "A1", "B5"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatType seatType = SeatType.REGULAR;

    public Seat() {}

    public Seat(Theater theater, String seatRow, Integer seatNumber, String seatCode, SeatType seatType) {
        this.theater = theater;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatCode = seatCode;
        this.seatType = seatType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Theater getTheater() { return theater; }
    public void setTheater(Theater theater) { this.theater = theater; }

    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }

    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
}
