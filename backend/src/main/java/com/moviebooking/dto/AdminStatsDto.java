package com.moviebooking.dto;

import java.math.BigDecimal;

public class AdminStatsDto {
    private long totalMovies;
    private long activeShowtimes;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private long totalUsers;

    public AdminStatsDto() {}

    public AdminStatsDto(long totalMovies, long activeShowtimes, long totalBookings, BigDecimal totalRevenue, long totalUsers) {
        this.totalMovies = totalMovies;
        this.activeShowtimes = activeShowtimes;
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalUsers = totalUsers;
    }

    public long getTotalMovies() { return totalMovies; }
    public void setTotalMovies(long totalMovies) { this.totalMovies = totalMovies; }

    public long getActiveShowtimes() { return activeShowtimes; }
    public void setActiveShowtimes(long activeShowtimes) { this.activeShowtimes = activeShowtimes; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
}
