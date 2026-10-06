package com.moviebooking.app.ui.views;

import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.Booking;
import com.moviebooking.app.model.Seat;
import com.moviebooking.app.model.Showtime;
import com.moviebooking.app.ui.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CheckoutDialog extends JDialog {

    private final Showtime showtime;
    private final List<Seat> selectedSeats;
    private boolean bookingCompleted = false;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy • h:mm a");

    public CheckoutDialog(Frame owner, Showtime showtime, List<Seat> selectedSeats) {
        super(owner, "Order Checkout - CineMagic", true);
        this.showtime = showtime;
        this.selectedSeats = selectedSeats;

        setSize(480, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header
        JLabel headLabel = new JLabel("Order Summary");
        headLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headLabel.setForeground(new Color(15, 23, 42));
        headLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subHead = new JLabel("Review and confirm your cinema tickets");
        subHead.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subHead.setForeground(new Color(100, 116, 139));
        subHead.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(headLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subHead);
        panel.add(Box.createVerticalStrut(18));

        // Movie Info Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel mTitle = new JLabel(showtime.getMovieTitle());
        mTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        mTitle.setForeground(new Color(15, 23, 42));

        JLabel tName = new JLabel(showtime.getTheaterName() + " (" + (showtime.getScreenType() != null ? showtime.getScreenType() : "Standard") + ")");
        tName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tName.setForeground(new Color(71, 85, 105));

        JLabel sTime = new JLabel(showtime.getStartTime() != null ? showtime.getStartTime().format(TIME_FMT) : "");
        sTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sTime.setForeground(new Color(100, 116, 139));

        card.add(mTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(tName);
        card.add(Box.createVerticalStrut(4));
        card.add(sTime);
        panel.add(card);
        panel.add(Box.createVerticalStrut(16));

        // Seats & Pricing Breakdown
        List<String> seatCodes = selectedSeats.stream().map(Seat::getSeatCode).sorted().collect(Collectors.toList());
        BigDecimal total = BigDecimal.ZERO;
        for (Seat s : selectedSeats) {
            total = total.add(s.getPrice());
        }

        panel.add(createRow("Selected Seats (" + selectedSeats.size() + ")", String.join(", ", seatCodes)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createRow("Ticket Subtotal", String.format("₹%.2f", total)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createRow("Convenience Fee & Taxes", "₹0.00 (Waived)"));
        panel.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(12));

        // Grand Total Row
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel totalLbl = new JLabel("Grand Total");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLbl.setForeground(new Color(15, 23, 42));

        JLabel totalVal = new JLabel(String.format("₹%.2f", total));
        totalVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalVal.setForeground(new Color(16, 185, 129));

        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(totalVal, BorderLayout.EAST);
        panel.add(totalRow);
        panel.add(Box.createVerticalStrut(16));

        // Payment Method Selector
        JLabel payLabel = new JLabel("Payment Method");
        payLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        payLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(payLabel);
        panel.add(Box.createVerticalStrut(6));

        JComboBox<String> payCombo = new JComboBox<>(new String[]{
                "Credit / Debit Card (Simulated Instant)",
                "Digital Wallet / UPI (Instant)",
                "Pay at Cinema Box Office Counter"
        });
        payCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        payCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(payCombo);
        panel.add(Box.createVerticalStrut(22));

        // Confirm Button
        ModernButton confirmBtn = new ModernButton("Pay & Confirm Booking", ModernButton.Style.SUCCESS);
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> executeBooking(confirmBtn));
        panel.add(confirmBtn);

        add(panel, BorderLayout.CENTER);
    }

    private JPanel createRow(String leftText, String rightText) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel left = new JLabel(leftText);
        left.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        left.setForeground(new Color(100, 116, 139));

        JLabel right = new JLabel(rightText);
        right.setFont(new Font("Segoe UI", Font.BOLD, 12));
        right.setForeground(new Color(15, 23, 42));

        r.add(left, BorderLayout.WEST);
        r.add(right, BorderLayout.EAST);
        return r;
    }

    private void executeBooking(ModernButton btn) {
        btn.setEnabled(false);
        btn.setText("Processing Transaction...");

        SwingUtilities.invokeLater(() -> {
            try {
                List<Long> seatIds = selectedSeats.stream().map(Seat::getId).collect(Collectors.toList());
                Booking booking = ApiClient.getInstance().bookTickets(showtime.getId(), seatIds);

                bookingCompleted = true;
                showSuccessDialog(booking);
                dispose();
            } catch (Exception ex) {
                btn.setEnabled(true);
                btn.setText("Pay & Confirm Booking");
                JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void showSuccessDialog(Booking booking) {
        String msg = String.format(
                "Booking Confirmed!\n\n" +
                "Reference Number: %s\n" +
                "Movie: %s\n" +
                "Screen: %s\n" +
                "Seats: %s\n" +
                "Amount Paid: ₹%.2f\n\n" +
                "Your tickets are saved to 'My Bookings'. Enjoy the movie!",
                booking.getBookingNumber(),
                booking.getMovieTitle(),
                booking.getTheaterName(),
                String.join(", ", booking.getSeatCodes()),
                booking.getTotalAmount()
        );
        JOptionPane.showMessageDialog(this, msg, "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isBookingCompleted() {
        return bookingCompleted;
    }
}
