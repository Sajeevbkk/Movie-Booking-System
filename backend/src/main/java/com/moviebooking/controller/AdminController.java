package com.moviebooking.controller;

import com.moviebooking.dto.AdminStatsDto;
import com.moviebooking.dto.UserDto;
import com.moviebooking.service.BookingService;
import com.moviebooking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookingService bookingService;
    private final UserService userService;

    public AdminController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(bookingService.getAdminStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String adminUsername = userDetails != null ? userDetails.getUsername() : "";
            userService.deleteUser(id, adminUsername);
            return ResponseEntity.ok(Map.of("message", "User and associated records removed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error removing user: " + e.getMessage()));
        }
    }
}
