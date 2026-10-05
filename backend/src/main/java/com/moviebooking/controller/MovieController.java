package com.moviebooking.controller;

import com.moviebooking.dto.MovieCreateRequest;
import com.moviebooking.dto.MovieDto;
import com.moviebooking.service.MovieService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // --- Public APIs ---

    @GetMapping("/movies")
    public ResponseEntity<List<MovieDto>> getAllActiveMovies() {
        return ResponseEntity.ok(movieService.getAllActiveMovies());
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(movieService.getMovieById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/movies/{id}/poster")
    public ResponseEntity<byte[]> getMoviePoster(@PathVariable Long id,
                                                 @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
        MovieService.PosterData poster = movieService.getPoster(id);
        if (poster == null || poster.getData() == null || poster.getData().length == 0) {
            return ResponseEntity.notFound().build();
        }

        String etag = "\"" + id + "-" + poster.getData().length + "\"";
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }

        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (poster.getContentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(poster.getContentType());
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic().mustRevalidate())
                .eTag(etag)
                .contentType(mediaType)
                .body(poster.getData());
    }

    // --- Admin APIs ---

    @GetMapping("/admin/movies")
    public ResponseEntity<List<MovieDto>> getAllMoviesAdmin() {
        return ResponseEntity.ok(movieService.getAllMoviesAdmin());
    }

    @PostMapping(value = "/admin/movies", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createMovie(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("genre") String genre,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "rating", required = false) String rating,
            @RequestParam(value = "releaseDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
            @RequestParam(value = "isActive", defaultValue = "true") Boolean isActive,
            @RequestParam(value = "poster", required = false) MultipartFile posterFile) {
        try {
            MovieCreateRequest req = new MovieCreateRequest();
            req.setTitle(title);
            req.setDescription(description);
            req.setGenre(genre);
            req.setDurationMinutes(durationMinutes);
            req.setLanguage(language);
            req.setRating(rating);
            req.setReleaseDate(releaseDate);
            req.setIsActive(isActive);

            MovieDto created = movieService.createMovie(req, posterFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error uploading poster: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping(value = "/admin/movies/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateMovie(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("genre") String genre,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "rating", required = false) String rating,
            @RequestParam(value = "releaseDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "poster", required = false) MultipartFile posterFile) {
        try {
            MovieCreateRequest req = new MovieCreateRequest();
            req.setTitle(title);
            req.setDescription(description);
            req.setGenre(genre);
            req.setDurationMinutes(durationMinutes);
            req.setLanguage(language);
            req.setRating(rating);
            req.setReleaseDate(releaseDate);
            req.setIsActive(isActive);

            MovieDto updated = movieService.updateMovie(id, req, posterFile);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error uploading poster: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(Map.of("message", "Movie deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
