package com.moviebooking.service;

import com.moviebooking.dto.AdminStatsDto;
import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.BookingResponseDto;
import com.moviebooking.entity.*;
import com.moviebooking.entity.enums.BookingStatus;
import com.moviebooking.entity.enums.PaymentStatus;
import com.moviebooking.entity.enums.Role;
import com.moviebooking.entity.enums.SeatType;
import com.moviebooking.entity.enums.ShowtimeStatus;
import com.moviebooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository,
                          BookingSeatRepository bookingSeatRepository,
                          SeatRepository seatRepository,
                          ShowtimeRepository showtimeRepository,
                          MovieRepository movieRepository,
                          UserRepository userRepository,
                          UserService userService) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public BookingResponseDto createBooking(String username, BookingRequest request) {
        User user = userService.getByUsername(username);

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));

        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        // Verify if any selected seat is already booked for this showtime
        boolean anyAlreadyBooked = bookingSeatRepository.existsByShowtimeIdAndSeatIdInAndBookingStatus(
                showtime.getId(), request.getSeatIds(), BookingStatus.CONFIRMED);
        if (anyAlreadyBooked) {
            throw new IllegalStateException("One or more selected seats have already been booked. Please choose other seats.");
        }

        List<Seat> seats = seatRepository.findByIdIn(request.getSeatIds());
        if (seats.size() != request.getSeatIds().size()) {
            throw new IllegalArgumentException("Invalid seat IDs provided");
        }

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BookingSeat> bookingSeats = new ArrayList<>();

        String bookingNumber = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = new Booking(
                bookingNumber,
                user,
                showtime,
                BigDecimal.ZERO,
                BookingStatus.CONFIRMED,
                PaymentStatus.PAID
        );
        Booking savedBooking = bookingRepository.save(booking);

        for (Seat seat : seats) {
            BigDecimal price = (seat.getSeatType() == SeatType.VIP) ? showtime.getVipPrice() : showtime.getRegularPrice();
            totalAmount = totalAmount.add(price);

            BookingSeat bookingSeat = new BookingSeat(savedBooking, showtime, seat, price);
            bookingSeats.add(bookingSeat);
        }

        bookingSeatRepository.saveAll(bookingSeats);
        savedBooking.setTotalAmount(totalAmount);
        savedBooking.setBookingSeats(bookingSeats);
        savedBooking = bookingRepository.save(savedBooking);

        return toDto(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getUserBookings(String username) {
        User user = userService.getByUsername(username);
        return bookingRepository.findByUserIdOrderByBookingTimeDesc(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsAdmin() {
        return bookingRepository.findAllByOrderByBookingTimeDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponseDto cancelBooking(String username, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        User user = userService.getByUsername(username);
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Not authorized to cancel this booking");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus(PaymentStatus.REFUNDED);
        Booking saved = bookingRepository.save(booking);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public AdminStatsDto getAdminStats() {
        long totalMovies = movieRepository.count();
        long activeShowtimes = showtimeRepository.findByStatusOrderByStartTimeAsc(ShowtimeStatus.ACTIVE).size();
        long totalBookings = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenueByStatus(BookingStatus.CONFIRMED);
        long totalUsers = userRepository.countByRole(Role.ROLE_USER);

        return new AdminStatsDto(totalMovies, activeShowtimes, totalBookings, totalRevenue, totalUsers);
    }

    private BookingResponseDto toDto(Booking b) {
        List<String> seatCodes = b.getBookingSeats().stream()
                .map(bs -> bs.getSeat().getSeatCode())
                .collect(Collectors.toList());

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(b.getId());
        dto.setBookingNumber(b.getBookingNumber());
        dto.setUserId(b.getUser().getId());
        dto.setUserName(b.getUser().getFullName());
        dto.setUserEmail(b.getUser().getEmail());
        dto.setMovieId(b.getShowtime().getMovie().getId());
        dto.setMovieTitle(b.getShowtime().getMovie().getTitle());
        dto.setTheaterId(b.getShowtime().getTheater().getId());
        dto.setTheaterName(b.getShowtime().getTheater().getName());
        dto.setScreenType(b.getShowtime().getTheater().getScreenType());
        dto.setShowtime(b.getShowtime().getStartTime());
        dto.setSeatCodes(seatCodes);
        dto.setTotalAmount(b.getTotalAmount());
        dto.setBookingStatus(b.getBookingStatus());
        dto.setPaymentStatus(b.getPaymentStatus());
        dto.setBookingTime(b.getBookingTime());
        return dto;
    }
}
