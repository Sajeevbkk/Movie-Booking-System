package com.moviebooking.controller;

import com.moviebooking.dto.SeatDto;
import com.moviebooking.dto.ShowtimeCreateRequest;
import com.moviebooking.dto.ShowtimeDto;
import com.moviebooking.service.ShowtimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    // --- Public APIs ---

    @GetMapping("/showtimes/movie/{movieId}")
    public ResponseEntity<List<ShowtimeDto>> getUpcomingByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.getUpcomingByMovie(movieId));
    }

    @GetMapping("/showtimes/upcoming")
    public ResponseEntity<List<ShowtimeDto>> getAllUpcoming() {
        return ResponseEntity.ok(showtimeService.getAllUpcoming());
    }

    @GetMapping("/showtimes/{id}")
    public ResponseEntity<?> getShowtimeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(showtimeService.getShowtimeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/showtimes/{id}/seats")
    public ResponseEntity<List<SeatDto>> getSeatsForShowtime(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(showtimeService.getSeatsWithStatus(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Admin APIs ---

    @GetMapping("/admin/showtimes")
    public ResponseEntity<List<ShowtimeDto>> getAllShowtimesAdmin() {
        return ResponseEntity.ok(showtimeService.getAllShowtimesAdmin());
    }

    @PostMapping("/admin/showtimes")
    public ResponseEntity<?> createShowtime(@RequestBody ShowtimeCreateRequest request) {
        try {
            ShowtimeDto created = showtimeService.createShowtime(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/showtimes/{id}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.ok(Map.of("message", "Showtime deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
