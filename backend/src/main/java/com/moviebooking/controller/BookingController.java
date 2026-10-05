package com.moviebooking.controller;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.BookingResponseDto;
import com.moviebooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody BookingRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User must be logged in to book tickets"));
        }
        try {
            BookingResponseDto booking = bookingService.createBooking(userDetails.getUsername(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings/my")
    public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
        }
        List<BookingResponseDto> bookings = bookingService.getUserBookings(userDetails.getUsername());
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
        }
        try {
            BookingResponseDto cancelled = bookingService.cancelBooking(userDetails.getUsername(), id);
            return ResponseEntity.ok(cancelled);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsAdmin() {
        return ResponseEntity.ok(bookingService.getAllBookingsAdmin());
    }
}
