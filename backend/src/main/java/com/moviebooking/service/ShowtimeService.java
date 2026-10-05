package com.moviebooking.service;

import com.moviebooking.dto.SeatDto;
import com.moviebooking.dto.ShowtimeCreateRequest;
import com.moviebooking.dto.ShowtimeDto;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.Theater;
import com.moviebooking.entity.enums.BookingStatus;
import com.moviebooking.entity.enums.SeatType;
import com.moviebooking.entity.enums.ShowtimeStatus;
import com.moviebooking.repository.BookingSeatRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.ShowtimeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final MovieService movieService;
    private final TheaterService theaterService;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                           SeatRepository seatRepository,
                           BookingSeatRepository bookingSeatRepository,
                           MovieService movieService,
                           TheaterService theaterService) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.movieService = movieService;
        this.theaterService = theaterService;
    }

    @Cacheable(value = "showtimes", key = "'movie_' + #movieId")
    @Transactional(readOnly = true)
    public List<ShowtimeDto> getUpcomingByMovie(Long movieId) {
        LocalDateTime now = LocalDateTime.now().minusMinutes(30);
        return showtimeRepository.findUpcomingByMovie(movieId, now, ShowtimeStatus.ACTIVE)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDto> getAllUpcoming() {
        LocalDateTime now = LocalDateTime.now().minusMinutes(30);
        return showtimeRepository.findUpcomingAll(now, ShowtimeStatus.ACTIVE)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDto> getAllShowtimesAdmin() {
        return showtimeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with id: " + id));
        return toDto(showtime);
    }

    public Showtime getShowtimeEntity(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsWithStatus(Long showtimeId) {
        Showtime showtime = getShowtimeEntity(showtimeId);
        Long theaterId = showtime.getTheater().getId();
        List<Seat> seats = seatRepository.findByTheaterIdOrderBySeatRowAscSeatNumberAsc(theaterId);

        // Fetch currently booked seats for this showtime
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeAndStatus(showtimeId, BookingStatus.CONFIRMED);
        Set<Long> bookedSet = new HashSet<>(bookedSeatIds);

        return seats.stream().map(seat -> {
            boolean isBooked = bookedSet.contains(seat.getId());
            BigDecimal price = (seat.getSeatType() == SeatType.VIP) ? showtime.getVipPrice() : showtime.getRegularPrice();
            return new SeatDto(
                    seat.getId(),
                    theaterId,
                    seat.getSeatRow(),
                    seat.getSeatNumber(),
                    seat.getSeatCode(),
                    seat.getSeatType(),
                    isBooked,
                    price
            );
        }).collect(Collectors.toList());
    }

    @CacheEvict(value = "showtimes", allEntries = true)
    @Transactional
    public ShowtimeDto createShowtime(ShowtimeCreateRequest request) {
        Movie movie = movieService.getMovieEntity(request.getMovieId());
        Theater theater = theaterService.getTheaterEntity(request.getTheaterId());

        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDurationMinutes() != null ? movie.getDurationMinutes() + 20 : 140);

        Showtime showtime = new Showtime(
                movie,
                theater,
                request.getStartTime(),
                endTime,
                request.getRegularPrice(),
                request.getVipPrice(),
                ShowtimeStatus.ACTIVE
        );

        Showtime saved = showtimeRepository.save(showtime);
        return toDto(saved);
    }

    @CacheEvict(value = "showtimes", allEntries = true)
    @Transactional
    public void deleteShowtime(Long id) {
        if (!showtimeRepository.existsById(id)) {
            throw new IllegalArgumentException("Showtime not found with id: " + id);
        }
        showtimeRepository.deleteById(id);
    }

    private ShowtimeDto toDto(Showtime s) {
        return new ShowtimeDto(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getTheater().getId(),
                s.getTheater().getName(),
                s.getTheater().getCity(),
                s.getTheater().getScreenType(),
                s.getStartTime(),
                s.getEndTime(),
                s.getRegularPrice(),
                s.getVipPrice(),
                s.getStatus()
        );
    }
}
