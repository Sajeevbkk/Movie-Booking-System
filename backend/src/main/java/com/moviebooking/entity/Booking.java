package com.moviebooking.entity;

import com.moviebooking.entity.enums.BookingStatus;
import com.moviebooking.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus bookingStatus = BookingStatus.CONFIRMED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    public Booking() {}

    public Booking(String bookingNumber, User user, Showtime showtime, BigDecimal totalAmount,
                   BookingStatus bookingStatus, PaymentStatus paymentStatus) {
        this.bookingNumber = bookingNumber;
        this.user = user;
        this.showtime = showtime;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus != null ? bookingStatus : BookingStatus.CONFIRMED;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PAID;
        this.bookingTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Showtime getShowtime() { return showtime; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public List<BookingSeat> getBookingSeats() { return bookingSeats; }
    public void setBookingSeats(List<BookingSeat> bookingSeats) { this.bookingSeats = bookingSeats; }
}
