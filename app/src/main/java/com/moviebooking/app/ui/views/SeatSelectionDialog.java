package com.moviebooking.app.ui.views;

import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.Seat;
import com.moviebooking.app.model.Showtime;
import com.moviebooking.app.ui.components.CurvedScreenPanel;
import com.moviebooking.app.ui.components.ModernButton;
import com.moviebooking.app.ui.components.SeatButton;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SeatSelectionDialog extends JDialog {

    private final Showtime showtime;
    private final Frame owner;
    private final List<SeatButton> selectedSeatButtons = new ArrayList<>();

    private JLabel selectedSeatsLabel;
    private JLabel totalAmountLabel;
    private ModernButton checkoutBtn;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a");

    public SeatSelectionDialog(Frame owner, Showtime showtime) {
        super(owner, "Select Seats - " + showtime.getMovieTitle(), true);
        this.owner = owner;
        this.showtime = showtime;

        setSize(850, 720);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel titleLabel = new JLabel(showtime.getMovieTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String showInfo = String.format("%s  •  %s  •  %s",
                showtime.getTheaterName(),
                showtime.getScreenType() != null ? showtime.getScreenType() : "Standard",
                showtime.getStartTime() != null ? showtime.getStartTime().format(TIME_FMT) : "");
        JLabel infoLabel = new JLabel(showInfo);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 116, 139));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(infoLabel);

        headerPanel.add(titleBox, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content (Curved Screen + Legend + Seat Grid)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(new Color(248, 250, 252));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Screen Panel
        JPanel topScreenBox = new JPanel(new BorderLayout());
        topScreenBox.setOpaque(false);
        topScreenBox.add(new CurvedScreenPanel(), BorderLayout.CENTER);
        centerPanel.add(topScreenBox, BorderLayout.NORTH);

        // Grid Container
        JPanel gridHolder = new JPanel(new GridBagLayout());
        gridHolder.setOpaque(false);
        JScrollPane gridScroll = new JScrollPane(gridHolder);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        gridScroll.setOpaque(false);
        gridScroll.getViewport().setOpaque(false);
        centerPanel.add(gridScroll, BorderLayout.CENTER);

        // Legend Panel
        JPanel legendPanel = createLegendPanel();
        centerPanel.add(legendPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Summary Bar
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);

        // Load Seats from Backend API
        loadSeats(gridHolder);
    }

    private JPanel createLegendPanel() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        legend.setOpaque(false);
        legend.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        legend.add(createLegendItem(new Color(255, 255, 255), new Color(203, 213, 225), "Regular (₹" + showtime.getRegularPrice() + ")"));
        legend.add(createLegendItem(new Color(254, 243, 199), new Color(245, 158, 11), "VIP (₹" + showtime.getVipPrice() + ")"));
        legend.add(createLegendItem(new Color(16, 185, 129), new Color(5, 150, 105), "Selected"));
        legend.add(createLegendItem(new Color(226, 232, 240), new Color(148, 163, 184), "Booked"));

        return legend;
    }

    private JPanel createLegendItem(Color bg, Color border, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(bg);
        box.setBorder(BorderFactory.createLineBorder(border, 1, true));
        box.setPreferredSize(new Dimension(16, 16));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));

        p.add(box);
        p.add(lbl);
        return p;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel infoBox = new JPanel();
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setOpaque(false);

        selectedSeatsLabel = new JLabel("Selected Seats: None");
        selectedSeatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedSeatsLabel.setForeground(new Color(15, 23, 42));

        totalAmountLabel = new JLabel("Total Amount: ₹0.00");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalAmountLabel.setForeground(new Color(16, 185, 129));

        infoBox.add(selectedSeatsLabel);
        infoBox.add(Box.createVerticalStrut(2));
        infoBox.add(totalAmountLabel);

        checkoutBtn = new ModernButton("Proceed to Checkout", ModernButton.Style.PRIMARY);
        checkoutBtn.setEnabled(false);
        checkoutBtn.setPreferredSize(new Dimension(180, 42));
        checkoutBtn.addActionListener(e -> proceedToCheckout());

        bar.add(infoBox, BorderLayout.WEST);
        bar.add(checkoutBtn, BorderLayout.EAST);

        return bar;
    }

    private void loadSeats(JPanel gridHolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Seat> seats = ApiClient.getInstance().getSeatsForShowtime(showtime.getId());
                renderSeatGrid(gridHolder, seats);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load auditorium seats: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void renderSeatGrid(JPanel gridHolder, List<Seat> seats) {
        gridHolder.removeAll();

        // Group seats by Row (e.g. A, B, C...)
        Map<String, List<Seat>> byRow = new TreeMap<>();
        for (Seat s : seats) {
            byRow.computeIfAbsent(s.getSeatRow(), k -> new ArrayList<>()).add(s);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        int rowIndex = 0;
        for (Map.Entry<String, List<Seat>> entry : byRow.entrySet()) {
            String rowName = entry.getKey();
            List<Seat> rowSeats = entry.getValue();
            rowSeats.sort(Comparator.comparingInt(Seat::getSeatNumber));

            // Row letter indicator on left
            gbc.gridx = 0;
            gbc.gridy = rowIndex;
            JLabel rowLbl = new JLabel(rowName);
            rowLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rowLbl.setForeground(new Color(100, 116, 139));
            gridHolder.add(rowLbl, gbc);

            int colIndex = 1;
            int middleAisle = rowSeats.size() / 2;

            for (Seat seat : rowSeats) {
                // Insert walkway gap in middle
                if (colIndex == middleAisle + 1) {
                    gbc.gridx = colIndex++;
                    gridHolder.add(Box.createHorizontalStrut(20), gbc);
                }

                SeatButton btn = new SeatButton(seat);
                btn.addItemListener(e -> onSeatToggled(btn));

                gbc.gridx = colIndex++;
                gridHolder.add(btn, gbc);
            }

            // Row letter indicator on right
            gbc.gridx = colIndex;
            JLabel rightRowLbl = new JLabel(rowName);
            rightRowLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rightRowLbl.setForeground(new Color(100, 116, 139));
            gridHolder.add(rightRowLbl, gbc);

            rowIndex++;
        }

        gridHolder.revalidate();
        gridHolder.repaint();
    }

    private void onSeatToggled(SeatButton btn) {
        if (btn.isSelected()) {
            selectedSeatButtons.add(btn);
        } else {
            selectedSeatButtons.remove(btn);
        }
        updateSelectionSummary();
    }

    private void updateSelectionSummary() {
        if (selectedSeatButtons.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalAmountLabel.setText("Total Amount: ₹0.00");
            checkoutBtn.setEnabled(false);
            return;
        }

        List<String> codes = selectedSeatButtons.stream()
                .map(b -> b.getSeat().getSeatCode())
                .sorted()
                .collect(Collectors.toList());

        BigDecimal total = BigDecimal.ZERO;
        for (SeatButton b : selectedSeatButtons) {
            total = total.add(b.getSeat().getPrice());
        }

        selectedSeatsLabel.setText("Selected Seats: " + String.join(", ", codes) + " (" + codes.size() + " seats)");
        totalAmountLabel.setText(String.format("Total Amount: ₹%.2f", total));
        checkoutBtn.setEnabled(true);
    }

    private void proceedToCheckout() {
        if (selectedSeatButtons.isEmpty()) return;

        // Check if user is logged in
        if (!ApiClient.getInstance().isAuthenticated()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "You need to sign in to complete your booking.\nWould you like to sign in now?",
                    "Authentication Required", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                LoginDialog loginDialog = new LoginDialog(owner, () -> {
                    RegisterDialog regDialog = new RegisterDialog(owner);
                    regDialog.setVisible(true);
                });
                loginDialog.setVisible(true);
                if (!ApiClient.getInstance().isAuthenticated()) {
                    return;
                }
            } else {
                return;
            }
        }

        List<Seat> seats = selectedSeatButtons.stream().map(SeatButton::getSeat).collect(Collectors.toList());
        CheckoutDialog checkout = new CheckoutDialog(owner, showtime, seats);
        checkout.setVisible(true);

        if (checkout.isBookingCompleted()) {
            dispose();
        }
    }
}
