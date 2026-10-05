package com.moviebooking.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.moviebooking.app.ui.views.MainFrame;

import javax.swing.*;
import java.awt.*;

public class MovieBookingApp {

    public static void main(String[] args) {
        // High-DPI and Text rendering hints
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        // Initialize FlatLaf Light Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf Look and Feel: " + e.getMessage());
        }

        // Setup custom UI defaults for crisp modern look
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

        // Launch Main Frame on EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
