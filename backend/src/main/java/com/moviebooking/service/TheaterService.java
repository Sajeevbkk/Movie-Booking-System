package com.moviebooking.service;

import com.moviebooking.dto.SeatDto;
import com.moviebooking.dto.TheaterDto;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Theater;
import com.moviebooking.entity.enums.SeatType;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.TheaterRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;

    public TheaterService(TheaterRepository theaterRepository, SeatRepository seatRepository) {
        this.theaterRepository = theaterRepository;
        this.seatRepository = seatRepository;
    }

    @Cacheable("theaters")
    @Transactional(readOnly = true)
    public List<TheaterDto> getAllTheaters() {
        return theaterRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Theater getTheaterEntity(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Theater not found with id: " + id));
    }

    @CacheEvict(value = "theaters", allEntries = true)
    @Transactional
    public TheaterDto createTheater(String name, String city, String location, String screenType, int rows, int cols) {
        Theater theater = new Theater(name, city, location, screenType, rows, cols);
        Theater saved = theaterRepository.save(theater);

        // Auto-generate standard seats layout
        List<Seat> seats = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            char rowChar = (char) ('A' + r);
            String rowStr = String.valueOf(rowChar);
            // Last 2 rows are VIP
            SeatType seatType = (r >= rows - 2) ? SeatType.VIP : SeatType.REGULAR;

            for (int c = 1; c <= cols; c++) {
                String seatCode = rowStr + c;
                seats.add(new Seat(saved, rowStr, c, seatCode, seatType));
            }
        }
        seatRepository.saveAll(seats);

        return toDto(saved);
    }

    public List<SeatDto> getTheaterSeats(Long theaterId) {
        return seatRepository.findByTheaterIdOrderBySeatRowAscSeatNumberAsc(theaterId)
                .stream()
                .map(seat -> new SeatDto(
                        seat.getId(),
                        seat.getTheater().getId(),
                        seat.getSeatRow(),
                        seat.getSeatNumber(),
                        seat.getSeatCode(),
                        seat.getSeatType(),
                        false,
                        null
                ))
                .collect(Collectors.toList());
    }

    private TheaterDto toDto(Theater theater) {
        int totalSeats = theater.getTotalRows() * theater.getTotalCols();
        return new TheaterDto(
                theater.getId(),
                theater.getName(),
                theater.getCity(),
                theater.getLocation(),
                theater.getScreenType(),
                theater.getTotalRows(),
                theater.getTotalCols(),
                totalSeats
        );
    }
}
