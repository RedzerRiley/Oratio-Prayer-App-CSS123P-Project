// File: src/main/java/com/oratio/utils/ModernUIUtils.java
package com.oratio.utils;

import com.oratio.services.ThemeService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Utility class for creating modern UI components and effects
 */
public class ModernUIUtils {

    /**
     * Creates a modern card panel with rounded corners and subtle shadow
     */
    public static JPanel createModernCard(String title, Component content) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        ThemeService themeService = ThemeService.getInstance();

        // Modern card styling with rounded corners
        card.setBackground(themeService.getCardBackgroundColor());
        card.setBorder(new RoundedBorder(12, themeService.getBorderColor()));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, themeService.getBorderColor()),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        if (title != null) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(themeService.getHeadingFont());
            titleLabel.setForeground(themeService.getForegroundColor());
            card.add(titleLabel, BorderLayout.NORTH);
        }

        if (content != null) {
            card.add(content, BorderLayout.CENTER);
        }

        return card;
    }

    /**
     * Creates a modern button with proper styling and hover effects
     */
    public static JButton createModernButton(String text, boolean isPrimary, String iconText) {
        JButton button = new JButton((iconText != null ? iconText + " " : "") + text);
        ThemeService themeService = ThemeService.getInstance();

        // Modern button styling
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(8, Color.WHITE));

        if (isPrimary) {
            button.setBackground(themeService.getAccentColor());
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(themeService.getBorderColor());
            button.setForeground(themeService.getForegroundColor());
        }

        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, Color.WHITE),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        addHoverEffect(button, isPrimary);
        return button;
    }

    /**
     * Creates a modern text field with rounded borders
     */
    public static JTextField createModernTextField(String placeholder) {
        JTextField textField = new JTextField();
        ThemeService themeService = ThemeService.getInstance();

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBackground(themeService.getCardBackgroundColor());
        textField.setForeground(themeService.getForegroundColor());
        textField.setCaretColor(themeService.getForegroundColor());

        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, themeService.getBorderColor()),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        if (placeholder != null) {
            addPlaceholder(textField, placeholder);
        }

        return textField;
    }

    /**
     * Creates a modern combo box with rounded styling
     */
    public static JComboBox<?> createModernComboBox(Object[] items) {
        JComboBox<Object> comboBox = new JComboBox<>(items);
        ThemeService themeService = ThemeService.getInstance();

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(themeService.getCardBackgroundColor());
        comboBox.setForeground(themeService.getForegroundColor());
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, themeService.getBorderColor()),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        comboBox.setFocusable(false);

        return comboBox;
    }

    /**
     * Adds a modern hover effect to buttons
     */
    private static void addHoverEffect(JButton button, boolean isPrimary) {
        ThemeService themeService = ThemeService.getInstance();
        Color originalBg = button.getBackground();
        Color originalFg = button.getForeground();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    if (isPrimary) {
                        button.setBackground(new Color(64, 75, 152));
                    } else {
                        button.setBackground(themeService.getSecondaryTextColor());
                        button.setForeground(Color.WHITE);
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
                button.setForeground(originalFg);
            }
        });
    }

    /**
     * Adds placeholder text to text fields
     */
    private static void addPlaceholder(JTextField textField, String placeholder) {
        ThemeService themeService = ThemeService.getInstance();
        Color placeholderColor = themeService.getSecondaryTextColor();
        Color normalColor = themeService.getForegroundColor();

        textField.setText(placeholder);
        textField.setForeground(placeholderColor);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(normalColor);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(placeholderColor);
                }
            }
        });
    }

    /**
     * Creates a modern separator line
     */
    public static JSeparator createModernSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(ThemeService.getInstance().getBorderColor());
        separator.setBackground(Color.white);
        return separator;
    }

    /**
     * Creates a modern loading indicator (simple animated dots)
     */
    public static JLabel createLoadingIndicator() {
        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadingLabel.setForeground(ThemeService.getInstance().getSecondaryTextColor());
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Simple animation timer for loading dots
        Timer timer = new Timer(500, e -> {
            String text = loadingLabel.getText();
            if (text.endsWith("...")) {
                loadingLabel.setText("Loading");
            } else {
                loadingLabel.setText(text + ".");
            }
        });
        timer.start();

        return loadingLabel;
    }

    /**
     * Custom rounded border class
     */
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;

        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (borderColor != Color.WHITE && borderColor != null) {
                g2d.setColor(borderColor);
                g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            }

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 1;
            return insets;
        }
    }

    /**
     * Creates a modern notification/toast component
     */
    public static void showModernNotification(JComponent parent, String message, NotificationType type) {
        JPanel notification = new JPanel(new BorderLayout(10, 0));
        ThemeService themeService = ThemeService.getInstance();

        // Set colors based on notification type
        Color bgColor, textColor;
        String icon;

        switch (type) {
            case SUCCESS:
                bgColor = new Color(40, 167, 69);
                textColor = Color.WHITE;
                icon = "✓";
                break;
            case WARNING:
                bgColor = new Color(255, 193, 7);
                textColor = new Color(33, 37, 41);
                icon = "⚠";
                break;
            case ERROR:
                bgColor = new Color(220, 53, 69);
                textColor = Color.WHITE;
                icon = "✗";
                break;
            default: // INFO
                bgColor = themeService.getAccentColor();
                textColor = Color.WHITE;
                icon = "ℹ";
                break;
        }

        notification.setBackground(bgColor);
        notification.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, Color.WHITE),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(textColor);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(textColor);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

        notification.add(iconLabel, BorderLayout.WEST);
        notification.add(messageLabel, BorderLayout.CENTER);

        // Show notification with fade effect
        showNotificationWithFade(parent, notification);
    }

    /**
     * Shows notification with fade in/out effect
     */
    private static void showNotificationWithFade(JComponent parent, JPanel notification) {
        JPanel overlay = new JPanel();
        overlay.setOpaque(false);
        overlay.setLayout(new BorderLayout());

        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setOpaque(false);
        container.add(notification);

        overlay.add(container, BorderLayout.NORTH);
        overlay.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        // ✅ Get the window ancestor (works for JFrame, JDialog, etc.)
        java.awt.Window window = SwingUtilities.getWindowAncestor(parent);
        if (window instanceof JFrame frame) {
            frame.getLayeredPane().add(overlay, JLayeredPane.POPUP_LAYER);
            overlay.setBounds(0, 0, frame.getWidth(), frame.getHeight());

            // Auto-hide after 3 seconds
            Timer hideTimer = new Timer(3000, e -> {
                frame.getLayeredPane().remove(overlay);
                frame.repaint();
            });
            hideTimer.setRepeats(false);
            hideTimer.start();

            parent.revalidate();
            parent.repaint();
        }
    }


    /**
     * Notification types
     */
    public enum NotificationType {
        SUCCESS, WARNING, ERROR, INFO
    }

    /**
     * Creates a modern progress bar
     */
    public static JProgressBar createModernProgressBar() {
        JProgressBar progressBar = new JProgressBar();
        ThemeService themeService = ThemeService.getInstance();

        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        progressBar.setForeground(themeService.getAccentColor());
        progressBar.setBackground(themeService.getBorderColor());
        progressBar.setBorder(new RoundedBorder(10, themeService.getBorderColor()));

        return progressBar;
    }

    /**
     * Applies modern list styling
     */
    public static void styleModernList(JList<?> list) {
        ThemeService themeService = ThemeService.getInstance();

        list.setBackground(themeService.getCardBackgroundColor());
        list.setForeground(themeService.getForegroundColor());
        list.setSelectionBackground(themeService.getAccentColor());
        list.setSelectionForeground(Color.WHITE);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        list.setFixedCellHeight(50);
    }

    /**
     * Applies modern text area styling
     */
    public static void styleModernTextArea(JTextArea textArea) {
        ThemeService themeService = ThemeService.getInstance();

        textArea.setBackground(themeService.getCardBackgroundColor());
        textArea.setForeground(themeService.getForegroundColor());
        textArea.setSelectionColor(themeService.getAccentColor());
        textArea.setSelectedTextColor(Color.WHITE);
        textArea.setCaretColor(themeService.getForegroundColor());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

}