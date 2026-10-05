package com.moviebooking.app.ui.views;

import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.AuthResponse;
import com.moviebooking.app.ui.components.ModernButton;

import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField fullNameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JLabel errorLabel;
    private boolean registerSuccessful = false;

    public RegisterDialog(Frame owner) {
        super(owner, "Create Account - CineMagic", true);

        setSize(400, 480);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        panel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Join CineMagic");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(15, 23, 42));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Create your profile to book movies instantly");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(100, 116, 139));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setVisible(false);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameField = createInputField("Full Name *", panel);
        emailField = createInputField("Email Address *", panel);
        usernameField = createInputField("Username *", panel);
        phoneField = createInputField("Phone Number", panel);

        // Password
        JLabel pLabel = new JLabel("Password *");
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(pLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(16));

        ModernButton registerBtn = new ModernButton("Create Account", ModernButton.Style.PRIMARY);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> performRegister());

        panel.add(registerBtn);

        add(panel, BorderLayout.CENTER);
    }

    private JTextField createInputField(String labelText, JPanel panel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(tf);
        panel.add(Box.createVerticalStrut(10));
        return tf;
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            showError("Please fill in all required fields (*)");
            return;
        }

        try {
            AuthResponse response = ApiClient.getInstance().register(username, password, fullName, email, phone);
            registerSuccessful = true;
            JOptionPane.showMessageDialog(this, "Account created successfully! Welcome, " + fullName, "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            showError("Registration error: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        revalidate();
    }

    public boolean isRegisterSuccessful() {
        return registerSuccessful;
    }
}
