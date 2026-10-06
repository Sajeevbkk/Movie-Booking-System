package com.moviebooking.service;

import com.moviebooking.dto.MovieCreateRequest;
import com.moviebooking.dto.MovieDto;
import com.moviebooking.entity.Booking;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Showtime;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ShowtimeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;

    public MovieService(MovieRepository movieRepository,
                        ShowtimeRepository showtimeRepository,
                        BookingRepository bookingRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingRepository = bookingRepository;
    }

    @Cacheable("movies")
    @Transactional(readOnly = true)
    public List<MovieDto> getAllActiveMovies() {
        return movieRepository.findByIsActiveTrueOrderByReleaseDateDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getAllMoviesAdmin() {
        return movieRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "movie_detail", key = "#id")
    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));
        return toDto(movie);
    }

    public Movie getMovieEntity(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));
    }

    @Cacheable(value = "posters", key = "#id")
    @Transactional(readOnly = true)
    public PosterData getPoster(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));
        if (movie.getPosterData() == null || movie.getPosterData().length == 0) {
            return null;
        }
        return new PosterData(movie.getPosterData(), movie.getPosterContentType() != null ? movie.getPosterContentType() : "image/jpeg");
    }

    @CacheEvict(value = {"movies", "movie_detail", "posters", "showtimes"}, allEntries = true)
    @Transactional
    public MovieDto createMovie(MovieCreateRequest request, MultipartFile posterFile) throws IOException {
        byte[] posterBytes = null;
        String contentType = null;
        if (posterFile != null && !posterFile.isEmpty()) {
            posterBytes = posterFile.getBytes();
            contentType = posterFile.getContentType();
        }

        Movie movie = new Movie(
                request.getTitle(),
                request.getDescription(),
                request.getGenre(),
                request.getDurationMinutes(),
                request.getLanguage(),
                request.getRating(),
                request.getReleaseDate(),
                posterBytes,
                contentType,
                request.getIsActive() != null ? request.getIsActive() : true
        );

        Movie saved = movieRepository.save(movie);
        return toDto(saved);
    }

    @CacheEvict(value = {"movies", "movie_detail", "posters", "showtimes"}, allEntries = true)
    @Transactional
    public MovieDto updateMovie(Long id, MovieCreateRequest request, MultipartFile posterFile) throws IOException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setLanguage(request.getLanguage());
        movie.setRating(request.getRating());
        movie.setReleaseDate(request.getReleaseDate());
        if (request.getIsActive() != null) {
            movie.setIsActive(request.getIsActive());
        }

        if (posterFile != null && !posterFile.isEmpty()) {
            movie.setPosterData(posterFile.getBytes());
            movie.setPosterContentType(posterFile.getContentType());
        }

        Movie updated = movieRepository.save(movie);
        return toDto(updated);
    }

    @CacheEvict(value = {"movies", "movie_detail", "posters", "showtimes"}, allEntries = true)
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));

        // Delete all showtimes for this movie, cascading through their bookings & booking seats
        List<Showtime> showtimes = showtimeRepository.findByMovieId(id);
        for (Showtime showtime : showtimes) {
            List<Booking> bookings = bookingRepository.findByShowtimeId(showtime.getId());
            bookingRepository.deleteAll(bookings);
        }
        showtimeRepository.deleteAll(showtimes);

        movieRepository.delete(movie);
    }

    private MovieDto toDto(Movie movie) {
        boolean hasPoster = movie.getPosterData() != null && movie.getPosterData().length > 0;
        String posterUrl = hasPoster ? "/api/movies/" + movie.getId() + "/poster" : null;
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getGenre(),
                movie.getDurationMinutes(),
                movie.getLanguage(),
                movie.getRating(),
                movie.getReleaseDate(),
                hasPoster,
                posterUrl,
                movie.getIsActive()
        );
    }

    public static class PosterData {
        private final byte[] data;
        private final String contentType;

        public PosterData(byte[] data, String contentType) {
            this.data = data;
            this.contentType = contentType;
        }

        public byte[] getData() { return data; }
        public String getContentType() { return contentType; }
    }
}
