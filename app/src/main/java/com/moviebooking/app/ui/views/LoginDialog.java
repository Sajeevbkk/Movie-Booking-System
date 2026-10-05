package com.moviebooking.app.ui.views;

import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.AuthResponse;
import com.moviebooking.app.ui.components.ModernButton;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private boolean loginSuccessful = false;
    private final Runnable onOpenRegister;

    public LoginDialog(Frame owner, Runnable onOpenRegister) {
        super(owner, "Sign In - CineMagic", true);
        this.onOpenRegister = onOpenRegister;

        setSize(380, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        panel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Welcome Back");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(15, 23, 42));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Sign in to manage and book your movie tickets");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(100, 116, 139));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setVisible(false);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = new JTextField("john_doe");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField("user123");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sign In Button
        ModernButton loginBtn = new ModernButton("Sign In", ModernButton.Style.PRIMARY);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());

        // Register link button
        JButton registerLinkBtn = new JButton("Don't have an account? Create one");
        registerLinkBtn.setBorderPainted(false);
        registerLinkBtn.setContentAreaFilled(false);
        registerLinkBtn.setForeground(new Color(37, 99, 235));
        registerLinkBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerLinkBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLinkBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerLinkBtn.addActionListener(e -> {
            dispose();
            if (onOpenRegister != null) onOpenRegister.run();
        });

        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(uLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(pLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(18));
        panel.add(loginBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerLinkBtn);

        add(panel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        try {
            AuthResponse response = ApiClient.getInstance().login(username, password);
            loginSuccessful = true;
            dispose();
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        revalidate();
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}
