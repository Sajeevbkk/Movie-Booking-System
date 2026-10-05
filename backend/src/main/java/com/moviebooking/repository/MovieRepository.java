package com.moviebooking.repository;

import com.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByIsActiveTrueOrderByReleaseDateDesc();
    List<Movie> findAllByOrderByCreatedAtDesc();

    @Query("SELECT m.posterData, m.posterContentType FROM Movie m WHERE m.id = :id")
    Optional<Object[]> findPosterById(@Param("id") Long id);
}
