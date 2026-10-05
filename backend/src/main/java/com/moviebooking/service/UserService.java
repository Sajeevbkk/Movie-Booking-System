package com.moviebooking.service;

import com.moviebooking.dto.AuthRequest;
import com.moviebooking.dto.AuthResponse;
import com.moviebooking.dto.RegisterRequest;
import com.moviebooking.dto.UserDto;
import com.moviebooking.entity.User;
import com.moviebooking.entity.enums.Role;
import com.moviebooking.config.JwtUtil;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       BookingRepository bookingRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                Role.ROLE_USER
        );

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());

        return new AuthResponse(token, saved.getId(), saved.getUsername(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRole());
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            int bookings = bookingRepository.findByUserIdOrderByBookingTimeDesc(user.getId()).size();
            return new UserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.getCreatedAt(),
                    bookings
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId, String adminUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (user.getUsername().equalsIgnoreCase(adminUsername)) {
            throw new IllegalArgumentException("Cannot delete currently logged-in administrator account");
        }

        // Delete all bookings of this user (cascades to booking_seats)
        List<com.moviebooking.entity.Booking> userBookings = bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);
        bookingRepository.deleteAll(userBookings);

        userRepository.deleteById(userId);
    }
}
