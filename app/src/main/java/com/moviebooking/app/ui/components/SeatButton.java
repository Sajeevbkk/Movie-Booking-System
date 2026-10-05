package com.moviebooking.app.ui.components;

import com.moviebooking.app.model.Seat;

import javax.swing.*;
import java.awt.*;

public class SeatButton extends JToggleButton {

    private final Seat seat;

    public SeatButton(Seat seat) {
        this.seat = seat;
        initComponent();
    }

    private void initComponent() {
        setPreferredSize(new Dimension(38, 38));
        setMargin(new Insets(2, 2, 2, 2));
        setFont(new Font("Segoe UI", Font.BOLD, 10));
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setText(seat.getSeatCode());
        setToolTipText(String.format("Seat %s [%s] - $%.2f",
                seat.getSeatCode(), seat.getSeatType(), seat.getPrice()));

        if (seat.isBooked()) {
            setEnabled(false);
            setBackground(new Color(226, 232, 240));
            setForeground(new Color(148, 163, 184));
            setToolTipText("Seat " + seat.getSeatCode() + " (Already Booked)");
            setCursor(Cursor.getDefaultCursor());
        } else {
            updateAppearance();
            addItemListener(e -> updateAppearance());
        }
    }

    private void updateAppearance() {
        if (seat.isBooked()) return;

        if (isSelected()) {
            // Selected state
            setBackground(new Color(16, 185, 129)); // Vibrant emerald green
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(5, 150, 105), 2, true));
        } else if (seat.isVip()) {
            // Available VIP state
            setBackground(new Color(254, 243, 199)); // Warm amber/gold
            setForeground(new Color(180, 83, 9));
            setBorder(BorderFactory.createLineBorder(new Color(245, 158, 11), 1, true));
        } else {
            // Available Regular state
            setBackground(Color.WHITE);
            setForeground(new Color(51, 65, 85));
            setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        }
    }

    public Seat getSeat() {
        return seat;
    }
}
