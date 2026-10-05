package com.moviebooking.app.ui.views;

import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.Booking;
import com.moviebooking.app.ui.components.ModernButton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyBookingsDialog extends JDialog {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private List<Booking> currentBookings;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a");

    public MyBookingsDialog(Frame owner) {
        super(owner, "My Bookings - CineMagic", true);

        setSize(850, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel title = new JLabel("My Cinema Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);

        ModernButton refreshBtn = new ModernButton("Refresh", ModernButton.Style.SECONDARY);
        refreshBtn.addActionListener(e -> loadBookings());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Booking Ref", "Movie", "Theater", "Screening Time", "Seats", "Total ($)", "Status", "Action"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        ModernButton cancelTicketBtn = new ModernButton("Cancel Selected Booking", ModernButton.Style.DANGER);
        cancelTicketBtn.addActionListener(e -> cancelSelectedBooking());

        ModernButton closeBtn = new ModernButton("Close", ModernButton.Style.SECONDARY);
        closeBtn.addActionListener(e -> dispose());

        bottomBar.add(cancelTicketBtn);
        bottomBar.add(closeBtn);
        add(bottomBar, BorderLayout.SOUTH);

        loadBookings();
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        SwingUtilities.invokeLater(() -> {
            try {
                currentBookings = ApiClient.getInstance().getMyBookings();
                for (Booking b : currentBookings) {
                    String timeStr = b.getShowtime() != null ? b.getShowtime().format(TIME_FMT) : "";
                    String seatsStr = b.getSeatCodes() != null ? String.join(", ", b.getSeatCodes()) : "";
                    String actionStr = b.isConfirmed() ? "Eligible for Refund" : "Refunded";

                    tableModel.addRow(new Object[]{
                            b.getBookingNumber(),
                            b.getMovieTitle(),
                            b.getTheaterName(),
                            timeStr,
                            seatsStr,
                            String.format("$%.2f", b.getTotalAmount()),
                            b.getBookingStatus(),
                            actionStr
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void cancelSelectedBooking() {
        int row = table.getSelectedRow();
        if (row < 0 || currentBookings == null || row >= currentBookings.size()) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to cancel.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Booking b = currentBookings.get(row);
        if (!b.isConfirmed()) {
            JOptionPane.showMessageDialog(this, "This booking has already been cancelled.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking " + b.getBookingNumber() + "?\nYour seats will be released and payment refunded.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                try {
                    ApiClient.getInstance().cancelBooking(b.getId());
                    JOptionPane.showMessageDialog(this, "Booking successfully cancelled. Seats have been released.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBookings();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Cancellation failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
