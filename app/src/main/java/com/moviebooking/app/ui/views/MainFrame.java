package com.moviebooking.app.ui.views;

import com.moviebooking.app.cache.DataCache;
import com.moviebooking.app.cache.PosterCache;
import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.Movie;
import com.moviebooking.app.model.Showtime;
import com.moviebooking.app.model.User;
import com.moviebooking.app.ui.components.ModernButton;
import com.moviebooking.app.ui.components.MovieCard;
import com.moviebooking.app.ui.components.WrapLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private final JPanel cardsContainer;
    private final JTextField searchField;
    private final JLabel userStatusLabel;
    private final ModernButton authButton;
    private final ModernButton myBookingsButton;

    private List<Movie> allMovies = new ArrayList<>();
    private String selectedGenre = "ALL";

    public MainFrame() {
        super("CineMagic - Modern Movie Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 780);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // 1. Top Navigation Bar
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        // Brand / Logo
        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandBox.setOpaque(false);
        JLabel logoIcon = new JLabel("🎬");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JLabel brandTitle = new JLabel("CineMagic Box Office");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(new Color(15, 23, 42));
        brandBox.add(logoIcon);
        brandBox.add(brandTitle);
        topBar.add(brandBox, BorderLayout.WEST);

        // Search Bar in Center
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        searchBox.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Search movies by title or genre...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterMovies(); }
            @Override public void removeUpdate(DocumentEvent e) { filterMovies(); }
            @Override public void changedUpdate(DocumentEvent e) { filterMovies(); }
        });
        searchBox.add(searchField);
        topBar.add(searchBox, BorderLayout.CENTER);

        // Right Actions (My Bookings, Refresh, User Profile / Auth)
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

        ModernButton refreshBtn = new ModernButton("Refresh", ModernButton.Style.SECONDARY);
        refreshBtn.addActionListener(e -> reloadMovies(true));

        myBookingsButton = new ModernButton("My Bookings", ModernButton.Style.OUTLINE);
        myBookingsButton.addActionListener(e -> openMyBookings());

        userStatusLabel = new JLabel("Guest");
        userStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userStatusLabel.setForeground(new Color(71, 85, 105));

        authButton = new ModernButton("Sign In", ModernButton.Style.PRIMARY);
        authButton.addActionListener(e -> handleAuthClick());

        rightActions.add(refreshBtn);
        rightActions.add(myBookingsButton);
        rightActions.add(Box.createHorizontalStrut(6));
        rightActions.add(userStatusLabel);
        rightActions.add(authButton);
        topBar.add(rightActions, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // 2. Center Content with Genre Filter Bar and Movie Cards Grid
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(248, 250, 252));

        // Genre Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        filterBar.setOpaque(false);
        filterBar.setBorder(BorderFactory.createEmptyBorder(6, 24, 0, 24));

        JLabel filterLabel = new JLabel("Categories:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(100, 116, 139));
        filterBar.add(filterLabel);

        String[] genres = {"ALL", "Sci-Fi", "Action", "Crime", "Adventure", "Thriller"};
        for (String g : genres) {
            JButton gBtn = new JButton(g.equals("ALL") ? "All Movies" : g);
            gBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gBtn.setFocusPainted(false);
            gBtn.putClientProperty("JButton.buttonType", "roundRect");
            gBtn.addActionListener(e -> {
                selectedGenre = g;
                filterMovies();
            });
            filterBar.add(gBtn);
        }
        centerPanel.add(filterBar, BorderLayout.NORTH);

        // Cards Grid inside Scroll Pane
        cardsContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 24, 24));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Initial Data Load
        updateAuthUI();
        reloadMovies(false);
    }

    private void reloadMovies(boolean force) {
        cardsContainer.removeAll();
        cardsContainer.add(new JLabel("Loading movies from server..."));
        cardsContainer.revalidate();
        cardsContainer.repaint();

        SwingUtilities.invokeLater(() -> {
            try {
                if (force) {
                    DataCache.getInstance().invalidate();
                }
                allMovies = DataCache.getInstance().getMovies(force);
                filterMovies();
            } catch (Exception ex) {
                cardsContainer.removeAll();
                JLabel err = new JLabel("Could not connect to backend server on port 8080: " + ex.getMessage());
                err.setForeground(new Color(239, 68, 68));
                cardsContainer.add(err);
                cardsContainer.revalidate();
                cardsContainer.repaint();
            }
        });
    }

    private void filterMovies() {
        cardsContainer.removeAll();
        String query = searchField.getText().trim().toLowerCase();

        List<Movie> filtered = allMovies.stream().filter(m -> {
            boolean matchesGenre = "ALL".equalsIgnoreCase(selectedGenre) ||
                    (m.getGenre() != null && m.getGenre().toLowerCase().contains(selectedGenre.toLowerCase()));
            boolean matchesSearch = query.isEmpty() ||
                    (m.getTitle() != null && m.getTitle().toLowerCase().contains(query)) ||
                    (m.getGenre() != null && m.getGenre().toLowerCase().contains(query));
            return matchesGenre && matchesSearch;
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("No movies match your filter criteria.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(100, 116, 139));
            cardsContainer.add(empty);
        } else {
            for (Movie movie : filtered) {
                MovieCard card = new MovieCard(movie, this::onBookMovie, this::onMovieDetails);
                cardsContainer.add(card);
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void onMovieDetails(Movie movie) {
        MovieDetailsDialog dialog = new MovieDetailsDialog(this, movie, this::onSelectShowtime);
        dialog.setVisible(true);
    }

    private void onBookMovie(Movie movie) {
        MovieDetailsDialog dialog = new MovieDetailsDialog(this, movie, this::onSelectShowtime);
        dialog.setVisible(true);
    }

    private void onSelectShowtime(Showtime showtime) {
        SeatSelectionDialog seatDialog = new SeatSelectionDialog(this, showtime);
        seatDialog.setVisible(true);
    }

    private void openMyBookings() {
        if (!ApiClient.getInstance().isAuthenticated()) {
            JOptionPane.showMessageDialog(this, "Please sign in to view your bookings.", "Sign In Required", JOptionPane.INFORMATION_MESSAGE);
            handleAuthClick();
            if (!ApiClient.getInstance().isAuthenticated()) return;
        }

        MyBookingsDialog bookingsDialog = new MyBookingsDialog(this);
        bookingsDialog.setVisible(true);
    }

    private void handleAuthClick() {
        if (ApiClient.getInstance().isAuthenticated()) {
            int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                ApiClient.getInstance().logout();
                updateAuthUI();
            }
        } else {
            LoginDialog login = new LoginDialog(this, () -> {
                RegisterDialog reg = new RegisterDialog(this);
                reg.setVisible(true);
                updateAuthUI();
            });
            login.setVisible(true);
            updateAuthUI();
        }
    }

    public void updateAuthUI() {
        if (ApiClient.getInstance().isAuthenticated()) {
            User u = ApiClient.getInstance().getCurrentUser();
            userStatusLabel.setText(u != null ? "Hello, " + u.getFullName() : "Logged In");
            authButton.setText("Sign Out");
        } else {
            userStatusLabel.setText("Guest");
            authButton.setText("Sign In");
        }
    }
}
