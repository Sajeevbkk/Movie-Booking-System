package com.moviebooking.repository;

import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.enums.ShowtimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieIdAndStatusOrderByStartTimeAsc(Long movieId, ShowtimeStatus status);
    List<Showtime> findByStatusOrderByStartTimeAsc(ShowtimeStatus status);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :fromTime AND s.status = :status ORDER BY s.startTime ASC")
    List<Showtime> findUpcomingByMovie(@Param("movieId") Long movieId, @Param("fromTime") LocalDateTime fromTime, @Param("status") ShowtimeStatus status);

    @Query("SELECT s FROM Showtime s WHERE s.startTime >= :fromTime AND s.status = :status ORDER BY s.startTime ASC")
    List<Showtime> findUpcomingAll(@Param("fromTime") LocalDateTime fromTime, @Param("status") ShowtimeStatus status);
}
