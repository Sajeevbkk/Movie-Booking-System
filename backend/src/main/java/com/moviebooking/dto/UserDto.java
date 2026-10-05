package com.moviebooking.dto;

import com.moviebooking.entity.enums.Role;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
    private int bookingCount;

    public UserDto() {}

    public UserDto(Long id, String username, String fullName, String email, String phone, Role role, LocalDateTime createdAt, int bookingCount) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
        this.bookingCount = bookingCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getBookingCount() { return bookingCount; }
    public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }
}
