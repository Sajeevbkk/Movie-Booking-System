package com.moviebooking.app.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.moviebooking.app.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ApiClient {

    private static ApiClient instance;

    private String baseUrl;
    private Duration timeout;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private String authToken;
    private User currentUser;

    private ApiClient() {
        loadConfiguration();

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(this.timeout)
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private void loadConfiguration() {
        Properties props = new Properties();
        File externalFile = new File("app.properties");

        // 1. Try external app.properties in working directory (for production overrides)
        if (externalFile.exists() && externalFile.isFile()) {
            try (InputStream in = new FileInputStream(externalFile)) {
                props.load(in);
                System.out.println("[CineMagic App] Loaded external configuration from: " + externalFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("[CineMagic App] Warning: Could not read external app.properties: " + e.getMessage());
            }
        } else {
            // 2. Fallback to classpath /app.properties packaged inside the application
            try (InputStream in = getClass().getResourceAsStream("/app.properties")) {
                if (in != null) {
                    props.load(in);
                    System.out.println("[CineMagic App] Loaded configuration from classpath app.properties");
                }
            } catch (Exception e) {
                System.err.println("[CineMagic App] Warning: Could not read classpath app.properties: " + e.getMessage());
            }
        }

        // Base URL
        String url = props.getProperty("api.base.url", "http://localhost:8080/api").trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        this.baseUrl = url;

        // Timeout
        int timeoutSec = 10;
        try {
            timeoutSec = Integer.parseInt(props.getProperty("api.timeout.seconds", "10").trim());
        } catch (Exception ignored) {}
        this.timeout = Duration.ofSeconds(timeoutSec);

        System.out.println("[CineMagic App] Backend API Target: " + this.baseUrl + " (Timeout: " + timeoutSec + "s)");
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null) {
            while (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            this.baseUrl = baseUrl;
        }
    }

    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setSession(String token, User user) {
        this.authToken = token;
        this.currentUser = user;
    }

    public void logout() {
        this.authToken = null;
        this.currentUser = null;
    }

    public AuthResponse login(String username, String password) throws IOException, InterruptedException {
        Map<String, String> body = Map.of("username", username, "password", password);
        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/login"))
                .header("Content-Type", "application/json")
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            AuthResponse auth = objectMapper.readValue(response.body(), AuthResponse.class);
            this.authToken = auth.getToken();
            this.currentUser = new User(auth.getId(), auth.getUsername(), auth.getFullName(), auth.getEmail(), null, auth.getRole());
            return auth;
        } else {
            String err = extractErrorMessage(response.body());
            throw new IOException(err);
        }
    }

    public AuthResponse register(String username, String password, String fullName, String email, String phone) throws IOException, InterruptedException {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("fullName", fullName);
        body.put("email", email);
        if (phone != null) body.put("phone", phone);

        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/register"))
                .header("Content-Type", "application/json")
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            AuthResponse auth = objectMapper.readValue(response.body(), AuthResponse.class);
            this.authToken = auth.getToken();
            this.currentUser = new User(auth.getId(), auth.getUsername(), auth.getFullName(), auth.getEmail(), phone, auth.getRole());
            return auth;
        } else {
            String err = extractErrorMessage(response.body());
            throw new IOException(err);
        }
    }

    public List<Movie> getMovies() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/movies"))
                .timeout(timeout)
                .GET();

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<Movie>>() {});
        } else {
            throw new IOException("Failed to load movies. Server returned status: " + response.statusCode());
        }
    }

    public List<Showtime> getShowtimesForMovie(Long movieId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/showtimes/movie/" + movieId))
                .timeout(timeout)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<Showtime>>() {});
        } else {
            throw new IOException("Failed to load showtimes");
        }
    }

    public List<Seat> getSeatsForShowtime(Long showtimeId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/showtimes/" + showtimeId + "/seats"))
                .timeout(timeout)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<Seat>>() {});
        } else {
            throw new IOException("Failed to load seats for showtime");
        }
    }

    public Booking bookTickets(Long showtimeId, List<Long> seatIds) throws IOException, InterruptedException {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Authentication required to book tickets");
        }

        Map<String, Object> body = Map.of("showtimeId", showtimeId, "seatIds", seatIds);
        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/bookings"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Booking.class);
        } else {
            String err = extractErrorMessage(response.body());
            throw new IOException(err);
        }
    }

    public List<Booking> getMyBookings() throws IOException, InterruptedException {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Authentication required to view your bookings");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/bookings/my"))
                .header("Authorization", "Bearer " + authToken)
                .timeout(timeout)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<Booking>>() {});
        } else {
            throw new IOException("Failed to fetch bookings");
        }
    }

    public Booking cancelBooking(Long bookingId) throws IOException, InterruptedException {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Authentication required");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/bookings/" + bookingId + "/cancel"))
                .header("Authorization", "Bearer " + authToken)
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Booking.class);
        } else {
            String err = extractErrorMessage(response.body());
            throw new IOException(err);
        }
    }

    public byte[] downloadPosterBytes(Long movieId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/movies/" + movieId + "/poster"))
                .timeout(timeout)
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    private String extractErrorMessage(String responseBody) {
        try {
            Map<String, Object> map = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            if (map.containsKey("error")) return String.valueOf(map.get("error"));
            if (map.containsKey("message")) return String.valueOf(map.get("message"));
        } catch (Exception ignored) {}
        return responseBody;
    }
}
