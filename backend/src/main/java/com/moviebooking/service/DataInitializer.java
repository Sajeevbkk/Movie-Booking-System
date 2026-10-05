package com.moviebooking.service;

import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.Theater;
import com.moviebooking.entity.User;
import com.moviebooking.entity.enums.Role;
import com.moviebooking.entity.enums.ShowtimeStatus;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.TheaterRepository;
import com.moviebooking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TheaterService theaterService;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.email:admin@cinemagic.com}")
    private String adminEmail;

    @Value("${admin.fullname:Cinema Administrator}")
    private String adminFullName;

    public DataInitializer(UserRepository userRepository,
                           MovieRepository movieRepository,
                           TheaterRepository theaterRepository,
                           ShowtimeRepository showtimeRepository,
                           TheaterService theaterService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.showtimeRepository = showtimeRepository;
        this.theaterService = theaterService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdminUser();
        initTheatersAndMovies();
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User(
                    adminUsername,
                    passwordEncoder.encode(adminPassword),
                    adminFullName,
                    adminEmail,
                    "+1-800-CINEMA",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            log.info("Initialized Admin account with username: '{}' from app.properties", adminUsername);
        }

        // Also ensure a test customer exists for convenience
        if (!userRepository.existsByUsername("john_doe")) {
            User user = new User(
                    "john_doe",
                    passwordEncoder.encode("user123"),
                    "John Doe",
                    "john@example.com",
                    "+1-555-0199",
                    Role.ROLE_USER
            );
            userRepository.save(user);
            log.info("Initialized demo customer account 'john_doe' / 'user123'");
        }
    }

    private void initTheatersAndMovies() {
        if (theaterRepository.count() == 0) {
            log.info("Seeding initial theaters...");
            theaterService.createTheater("Screen 1 - Dolby Atmos 4K", "Downtown Cineplex", "Main St, Downtown", "Dolby Atmos 4K Laser", 8, 10);
            theaterService.createTheater("Screen 2 - IMAX Laser 3D", "Downtown Cineplex", "Main St, Downtown", "IMAX Laser 3D", 8, 12);
        }

        if (movieRepository.count() == 0) {
            log.info("Seeding initial movies with generated binary posters into MySQL database...");

            byte[] poster1 = generateMoviePosterImage("INCEPTION", "Mind-bending Thriller", new Color(15, 32, 67), new Color(40, 116, 240));
            byte[] poster2 = generateMoviePosterImage("INTERSTELLAR", "Humanity's Next Journey", new Color(10, 10, 25), new Color(120, 60, 180));
            byte[] poster3 = generateMoviePosterImage("THE DARK KNIGHT", "The Hero Gotham Deserves", new Color(20, 20, 20), new Color(220, 160, 20));
            byte[] poster4 = generateMoviePosterImage("AVATAR 2", "Way of Water", new Color(5, 40, 60), new Color(0, 190, 220));

            Movie m1 = new Movie("Inception",
                    "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
                    "Sci-Fi / Thriller", 148, "English", "8.8/10 (PG-13)",
                    LocalDate.of(2010, 7, 16), poster1, "image/png", true);

            Movie m2 = new Movie("Interstellar",
                    "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival as Earth faces catastrophic collapse.",
                    "Sci-Fi / Adventure", 169, "English", "8.7/10 (PG-13)",
                    LocalDate.of(2014, 11, 7), poster2, "image/png", true);

            Movie m3 = new Movie("The Dark Knight",
                    "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.",
                    "Action / Crime", 152, "English", "9.0/10 (PG-13)",
                    LocalDate.of(2008, 7, 18), poster3, "image/png", true);

            Movie m4 = new Movie("Avatar: The Way of Water",
                    "Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri.",
                    "Action / Adventure", 192, "English", "7.8/10 (PG-13)",
                    LocalDate.of(2022, 12, 16), poster4, "image/png", true);

            movieRepository.saveAll(List.of(m1, m2, m3, m4));
            log.info("Seeded 4 movies with binary posters into MySQL!");
        }

        if (showtimeRepository.count() == 0) {
            log.info("Scheduling upcoming showtimes...");
            List<Theater> theaters = theaterRepository.findAll();
            List<Movie> movies = movieRepository.findAll();

            if (!theaters.isEmpty() && movies.size() >= 4) {
                Theater t1 = theaters.get(0);
                Theater t2 = theaters.size() > 1 ? theaters.get(1) : t1;

                LocalDateTime today = LocalDateTime.now().withHour(11).withMinute(0).withSecond(0).withNano(0);
                if (today.isBefore(LocalDateTime.now())) {
                    today = today.plusDays(1);
                }

                // Schedule showtimes
                Showtime s1 = new Showtime(movies.get(0), t1, today.withHour(11).withMinute(30), today.withHour(14).withMinute(0),
                        new BigDecimal("12.50"), new BigDecimal("18.00"), ShowtimeStatus.ACTIVE);
                Showtime s2 = new Showtime(movies.get(0), t2, today.withHour(15).withMinute(0), today.withHour(17).withMinute(30),
                        new BigDecimal("14.00"), new BigDecimal("20.00"), ShowtimeStatus.ACTIVE);
                Showtime s3 = new Showtime(movies.get(1), t1, today.withHour(18).withMinute(0), today.withHour(21).withMinute(0),
                        new BigDecimal("13.00"), new BigDecimal("19.50"), ShowtimeStatus.ACTIVE);
                Showtime s4 = new Showtime(movies.get(2), t2, today.withHour(19).withMinute(30), today.withHour(22).withMinute(15),
                        new BigDecimal("14.50"), new BigDecimal("21.00"), ShowtimeStatus.ACTIVE);
                Showtime s5 = new Showtime(movies.get(3), t1, today.plusDays(1).withHour(14).withMinute(0), today.plusDays(1).withHour(17).withMinute(30),
                        new BigDecimal("15.00"), new BigDecimal("22.00"), ShowtimeStatus.ACTIVE);

                showtimeRepository.saveAll(List.of(s1, s2, s3, s4, s5));
                log.info("Seeded 5 upcoming showtimes!");
            }
        }
    }

    private byte[] generateMoviePosterImage(String title, String tag, Color topColor, Color accentColor) {
        int width = 360;
        int height = 540;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Gradient background
        GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, new Color(10, 12, 16));
        g.setPaint(gp);
        g.fillRect(0, 0, width, height);

        // Accent decorative banner
        g.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 70));
        g.fillRoundRect(20, 30, width - 40, height - 60, 24, 24);
        g.setColor(accentColor);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(20, 30, width - 40, height - 60, 24, 24);

        // Cinema Badge at Top
        g.setColor(new Color(255, 255, 255, 200));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fmBadge = g.getFontMetrics();
        String badge = "★ CINEMAGIC PREMIERE ★";
        g.drawString(badge, (width - fmBadge.stringWidth(badge)) / 2, 70);

        // Center Abstract Artwork Shapes
        g.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 120));
        g.fillOval(width / 2 - 70, height / 2 - 100, 140, 140);
        g.setColor(new Color(255, 255, 255, 180));
        g.drawOval(width / 2 - 50, height / 2 - 80, 100, 100);

        // Play / Cinema icon in circle
        Polygon triangle = new Polygon();
        triangle.addPoint(width / 2 - 10, height / 2 - 45);
        triangle.addPoint(width / 2 - 10, height / 2 - 15);
        triangle.addPoint(width / 2 + 18, height / 2 - 30);
        g.setColor(Color.WHITE);
        g.fillPolygon(triangle);

        // Movie Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        FontMetrics fmTitle = g.getFontMetrics();
        int titleX = (width - fmTitle.stringWidth(title)) / 2;
        g.drawString(title, titleX, height / 2 + 80);

        // Tagline / Subtitle
        g.setColor(new Color(200, 210, 225));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        FontMetrics fmTag = g.getFontMetrics();
        int tagX = (width - fmTag.stringWidth(tag)) / 2;
        g.drawString(tag, tagX, height / 2 + 110);

        // Footer info bar
        g.setColor(new Color(255, 255, 255, 30));
        g.fillRoundRect(35, height - 90, width - 70, 36, 12, 12);
        g.setColor(new Color(230, 230, 230));
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        String footer = "NOW SHOWING IN CINEMAS";
        FontMetrics fmFoot = g.getFontMetrics();
        g.drawString(footer, (width - fmFoot.stringWidth(footer)) / 2, height - 68);

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate poster image", e);
            return new byte[0];
        }
    }
}
