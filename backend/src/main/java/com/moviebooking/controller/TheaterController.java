package com.moviebooking.controller;

import com.moviebooking.dto.SeatDto;
import com.moviebooking.dto.TheaterDto;
import com.moviebooking.service.TheaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping("/theaters")
    public ResponseEntity<List<TheaterDto>> getAllTheaters() {
        return ResponseEntity.ok(theaterService.getAllTheaters());
    }

    @GetMapping("/theaters/{id}/seats")
    public ResponseEntity<List<SeatDto>> getTheaterSeats(@PathVariable Long id) {
        return ResponseEntity.ok(theaterService.getTheaterSeats(id));
    }

    @PostMapping("/admin/theaters")
    public ResponseEntity<?> createTheater(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String city = (String) body.get("city");
            String location = (String) body.get("location");
            String screenType = (String) body.get("screenType");
            int rows = Integer.parseInt(body.getOrDefault("rows", 8).toString());
            int cols = Integer.parseInt(body.getOrDefault("cols", 10).toString());

            TheaterDto created = theaterService.createTheater(name, city, location, screenType, rows, cols);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
