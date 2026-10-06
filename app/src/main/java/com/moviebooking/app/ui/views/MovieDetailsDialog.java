package com.moviebooking.app.ui.views;

import com.moviebooking.app.cache.PosterCache;
import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.Movie;
import com.moviebooking.app.model.Showtime;
import com.moviebooking.app.ui.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class MovieDetailsDialog extends JDialog {

    private final Movie movie;
    private final Consumer<Showtime> onSelectShowtime;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a");

    public MovieDetailsDialog(Frame owner, Movie movie, Consumer<Showtime> onSelectShowtime) {
        super(owner, movie.getTitle() + " - CineMagic", true);
        this.movie = movie;
        this.onSelectShowtime = onSelectShowtime;

        setSize(700, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Left Poster Panel
        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(200, 300));
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        PosterCache.getInstance().loadPosterAsync(movie.getId(), 200, 300, posterLabel::setIcon);
        mainPanel.add(posterLabel, BorderLayout.WEST);

        // Right Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));

        String meta = String.format("%s  •  %dm  •  %s  •  %s",
                movie.getGenre() != null ? movie.getGenre() : "",
                movie.getDurationMinutes() != null ? movie.getDurationMinutes() : 0,
                movie.getLanguage() != null ? movie.getLanguage() : "English",
                movie.getRating() != null ? movie.getRating() : "PG");
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        metaLabel.setForeground(new Color(100, 116, 139));

        JLabel descTitle = new JLabel("Synopsis");
        descTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descTitle.setForeground(new Color(15, 23, 42));

        JTextArea descArea = new JTextArea(movie.getDescription() != null ? movie.getDescription() : "No synopsis available.");
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setForeground(new Color(71, 85, 105));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(metaLabel);
        infoPanel.add(Box.createVerticalStrut(14));
        infoPanel.add(descTitle);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(descArea);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Bottom Showtimes Section
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Available Screenings & Showtimes"));

        DefaultListModel<Showtime> showtimeListModel = new DefaultListModel<>();
        JList<Showtime> showtimeJList = new JList<>(showtimeListModel);
        showtimeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showtimeJList.setCellRenderer(new ShowtimeCellRenderer());
        JScrollPane scrollPane = new JScrollPane(showtimeJList);
        scrollPane.setPreferredSize(new Dimension(650, 140));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        ModernButton selectSeatsBtn = new ModernButton("Select Seats & Book", ModernButton.Style.PRIMARY);
        selectSeatsBtn.setEnabled(false);

        showtimeJList.addListSelectionListener(e -> {
            selectSeatsBtn.setEnabled(!showtimeJList.isSelectionEmpty());
        });

        selectSeatsBtn.addActionListener(e -> {
            Showtime selected = showtimeJList.getSelectedValue();
            if (selected != null) {
                dispose();
                if (onSelectShowtime != null) onSelectShowtime.accept(selected);
            }
        });

        actionPanel.add(selectSeatsBtn);

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        // Fetch showtimes in background
        SwingUtilities.invokeLater(() -> {
            try {
                List<Showtime> showtimes = ApiClient.getInstance().getShowtimesForMovie(movie.getId());
                showtimes.forEach(showtimeListModel::addElement);
                if (showtimes.isEmpty()) {
                    showtimeJList.setToolTipText("No showtimes currently scheduled for this movie.");
                } else {
                    showtimeJList.setSelectedIndex(0);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not load showtimes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static class ShowtimeCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Showtime s) {
                String text = String.format("  %s  |  %s (%s)  |  Regular: ₹%.2f  •  VIP: ₹%.2f",
                        s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : "TBD",
                        s.getTheaterName(),
                        s.getScreenType() != null ? s.getScreenType() : "Standard",
                        s.getRegularPrice(),
                        s.getVipPrice());
                setText(text);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            }
            return this;
        }
    }
}
