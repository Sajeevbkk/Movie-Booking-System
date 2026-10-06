package com.moviebooking.repository;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);
    List<Booking> findByShowtimeId(Long showtimeId);
    List<Booking> findAllByOrderByBookingTimeDesc();
    Optional<Booking> findByBookingNumber(String bookingNumber);
    long countByBookingStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = :status")
    BigDecimal sumTotalRevenueByStatus(@Param("status") BookingStatus status);
}
