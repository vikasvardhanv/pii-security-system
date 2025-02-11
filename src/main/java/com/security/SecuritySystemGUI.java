package com.security;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class SecuritySystemGUI extends JFrame {
    private PIISecuritySystem securitySystem;
    private JTextField serviceField;
    private JPasswordField passwordField;
    private JTextArea logArea;
    private JTextField retrieveServiceField;
    private JPanel mainPanel;

    public SecuritySystemGUI() {
        try {
            securitySystem = new PIISecuritySystem();
            initializeUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize security system: " + e.getMessage(),
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeUI() {
        setTitle("Security System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 500));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Password Management", createPasswordPanel());
        tabbedPane.addTab("PII Monitor", createPIIPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("System Log"));
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Save Password Section
        JPanel savePanel = new JPanel(new GridBagLayout());
        savePanel.setBorder(BorderFactory.createTitledBorder("Save Password"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        savePanel.add(new JLabel("Service:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        serviceField = new JTextField(20);
        savePanel.add(serviceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        savePanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        savePanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = new JButton("Save Password");
        saveButton.addActionListener(e -> savePassword());
        savePanel.add(saveButton, gbc);

        // Retrieve Password Section
        JPanel retrievePanel = new JPanel(new GridBagLayout());
        retrievePanel.setBorder(BorderFactory.createTitledBorder("Retrieve Password"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        retrievePanel.add(new JLabel("Service:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        retrieveServiceField = new JTextField(20);
        retrievePanel.add(retrieveServiceField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton retrieveButton = new JButton("Retrieve Password");
        retrieveButton.addActionListener(e -> retrievePassword());
        retrievePanel.add(retrieveButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(savePanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 5, 5, 5);
        panel.add(retrievePanel, gbc);

        return panel;
    }

    private JPanel createPIIPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField piiInputField = new JTextField();
        JButton checkButton = new JButton("Check for PII");

        checkButton.addActionListener(e -> {
            String input = piiInputField.getText();
            boolean containsPII = securitySystem.monitorInput(input);
            if (containsPII) {
                logMessage("PII detected in input!");
                piiInputField.setText("");
            } else {
                logMessage("No PII detected in input");
            }
        });

        panel.add(new JLabel("Enter text to check for PII:"), BorderLayout.NORTH);
        panel.add(piiInputField, BorderLayout.CENTER);
        panel.add(checkButton, BorderLayout.SOUTH);

        return panel;
    }

    private void savePassword() {
        String service = serviceField.getText();
        String password = new String(passwordField.getPassword());

        if (service.isEmpty() || password.isEmpty()) {
            logMessage("Service and password cannot be empty");
            return;
        }

        try {
            securitySystem.savePassword(service, password);
            logMessage("Password saved for service: " + service);
            serviceField.setText("");
            passwordField.setText("");
        } catch (Exception e) {
            logMessage("Error: " + e.getMessage());
        }
    }

    private void retrievePassword() {
        String service = retrieveServiceField.getText();

        if (service.isEmpty()) {
            logMessage("Please enter a service name");
            return;
        }

        try {
            String password = securitySystem.getPassword(service);
            logMessage("Retrieved password for service: " + service);
            JOptionPane.showMessageDialog(this,
                    "Password: " + password,
                    "Retrieved Password",
                    JOptionPane.INFORMATION_MESSAGE);
            retrieveServiceField.setText("");
        } catch (Exception e) {
            logMessage("Error: " + e.getMessage());
        }
    }

    private void logMessage(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SecuritySystemGUI().setVisible(true);
        });
    }
}