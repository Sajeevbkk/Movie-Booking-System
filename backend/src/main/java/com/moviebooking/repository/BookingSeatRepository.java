package com.moviebooking.repository;

import com.moviebooking.entity.BookingSeat;
import com.moviebooking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    @Query("SELECT bs.seat.id FROM BookingSeat bs WHERE bs.showtime.id = :showtimeId AND bs.booking.bookingStatus = :status")
    List<Long> findBookedSeatIdsByShowtimeAndStatus(@Param("showtimeId") Long showtimeId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(bs) > 0 FROM BookingSeat bs WHERE bs.showtime.id = :showtimeId AND bs.seat.id IN :seatIds AND bs.booking.bookingStatus = :status")
    boolean existsByShowtimeIdAndSeatIdInAndBookingStatus(@Param("showtimeId") Long showtimeId,
                                                         @Param("seatIds") List<Long> seatIds,
                                                         @Param("status") BookingStatus status);
}
