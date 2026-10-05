package com.moviebooking.app.ui.components;

import com.moviebooking.app.cache.PosterCache;
import com.moviebooking.app.model.Movie;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MovieCard extends JPanel {

    private final Movie movie;
    private final Consumer<Movie> onBookAction;
    private final Consumer<Movie> onDetailsAction;
    private final JLabel posterLabel;

    public MovieCard(Movie movie, Consumer<Movie> onBookAction, Consumer<Movie> onDetailsAction) {
        this.movie = movie;
        this.onBookAction = onBookAction;
        this.onDetailsAction = onDetailsAction;

        setLayout(new BorderLayout(0, 8));
        setPreferredSize(new Dimension(210, 365));
        setMaximumSize(new Dimension(210, 365));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Poster Image Label
        posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(190, 230));
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setBackground(new Color(241, 245, 249));
        posterLabel.setOpaque(true);
        posterLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        posterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onDetailsAction != null) onDetailsAction.accept(movie);
            }
        });

        // Load poster via Dual-Tier Cache
        PosterCache.getInstance().loadPosterAsync(movie.getId(), 190, 230, posterLabel::setIcon);
        add(posterLabel, BorderLayout.NORTH);

        // Info & Action Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setToolTipText(movie.getTitle());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Genre & Duration
        String subtitle = (movie.getGenre() != null ? movie.getGenre() : "") + " • " + movie.getDurationMinutes() + "m";
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(100, 116, 139));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Rating Badge
        String ratingText = movie.getRating() != null ? "★ " + movie.getRating() : "★ Featured";
        JLabel ratingLabel = new JLabel(ratingText);
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        ratingLabel.setForeground(new Color(217, 119, 6)); // Amber
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(subtitleLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(ratingLabel);
        infoPanel.add(Box.createVerticalStrut(6));

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton detailsBtn = new ModernButton("Info", ModernButton.Style.SECONDARY);
        detailsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailsBtn.addActionListener(e -> {
            if (onDetailsAction != null) onDetailsAction.accept(movie);
        });

        ModernButton bookBtn = new ModernButton("Book", ModernButton.Style.PRIMARY);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        bookBtn.addActionListener(e -> {
            if (onBookAction != null) onBookAction.accept(movie);
        });

        btnPanel.add(detailsBtn);
        btnPanel.add(bookBtn);
        infoPanel.add(btnPanel);

        add(infoPanel, BorderLayout.CENTER);
    }

    public Movie getMovie() {
        return movie;
    }
}
