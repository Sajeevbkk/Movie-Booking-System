package com.moviebooking.app.ui.components;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {

    public enum Style {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        DANGER,
        OUTLINE
    }

    private final Style style;

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        initStyle();
    }

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    private void initStyle() {
        setFocusPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        putClientProperty("JButton.buttonType", "roundRect");
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        switch (style) {
            case PRIMARY -> {
                setBackground(new Color(37, 99, 235));
                setForeground(Color.WHITE);
            }
            case SECONDARY -> {
                setBackground(new Color(241, 245, 249));
                setForeground(new Color(51, 65, 85));
            }
            case SUCCESS -> {
                setBackground(new Color(16, 185, 129));
                setForeground(Color.WHITE);
            }
            case DANGER -> {
                setBackground(new Color(239, 68, 68));
                setForeground(Color.WHITE);
            }
            case OUTLINE -> {
                setBackground(Color.WHITE);
                setForeground(new Color(37, 99, 235));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(37, 99, 235), 1, true),
                        BorderFactory.createEmptyBorder(7, 15, 7, 15)
                ));
            }
        }
    }
}
