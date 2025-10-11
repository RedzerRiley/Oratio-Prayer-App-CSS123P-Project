// File: src/main/java/com/oratio/services/ThemeService.java
package com.oratio.services;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Service for handling light/dark theme switching with modern design
 */
public class ThemeService {
    private static ThemeService instance;
    private boolean isDarkMode = false;

    // Modern theme colors
    private final Color LIGHT_BACKGROUND = Color.WHITE;
    private final Color LIGHT_FOREGROUND = new Color(33, 37, 41);
    private final Color LIGHT_PANEL_BG = new Color(248, 249, 250);
    private final Color LIGHT_CARD_BG = Color.WHITE;
    private final Color LIGHT_BORDER = new Color(222, 226, 230);
    private final Color LIGHT_TEXT_SECONDARY = new Color(108, 117, 125);

    private final Color DARK_BACKGROUND = new Color(33, 37, 41);
    private final Color DARK_FOREGROUND = new Color(248, 249, 250);
    private final Color DARK_PANEL_BG = new Color(45, 52, 54);
    private final Color DARK_CARD_BG = new Color(52, 58, 64);
    private final Color DARK_BORDER = new Color(73, 80, 87);
    private final Color DARK_TEXT_SECONDARY = new Color(173, 181, 189);

    // Accent colors
    private final Color ACCENT_PRIMARY = new Color(74, 85, 162);
    private final Color ACCENT_SUCCESS = new Color(40, 167, 69);
    private final Color ACCENT_WARNING = new Color(255, 193, 7);

    private ThemeService() {
    }

    public static ThemeService getInstance() {
        if (instance == null) {
            instance = new ThemeService();
        }
        return instance;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleTheme() {
        isDarkMode = !isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    // Modern color getters
    public Color getBackgroundColor() {
        return isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public Color getForegroundColor() {
        return isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND;
    }

    public Color getPanelBackgroundColor() {
        return isDarkMode ? DARK_PANEL_BG : LIGHT_PANEL_BG;
    }

    public Color getCardBackgroundColor() {
        return isDarkMode ? DARK_CARD_BG : LIGHT_CARD_BG;
    }

    public Color getBorderColor() {
        return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
    }

    public Color getSecondaryTextColor() {
        return isDarkMode ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public Color getAccentColor() {
        return ACCENT_PRIMARY;
    }

    public Font getHeadingFont() {
        return new Font("Segoe UI", Font.BOLD, 16);
    }

    public Font getBodyFont() {
        return new Font("Segoe UI", Font.PLAIN, 12);
    }

    public Font getLabelFont() {
        return new Font("Segoe UI", Font.BOLD, 11);
    }

    public void applyTheme(Component component) {
        applyModernThemeRecursively(component);
    }

    public void applyTheme(JFrame frame) {
        UIManager.put("darkMode", isDarkMode);
        applyModernThemeRecursively(frame);
    }

    private void applyModernThemeRecursively(Component component) {
        if (component == null) {
            return;
        }

        // Apply theme to the component itself
        applyThemeToComponent(component);

        // Recursively apply to children
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                applyModernThemeRecursively(child);
            }
        }

        // Special handling for compound components
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            applyModernThemeRecursively(scrollPane.getViewport().getView());
        }

        if (component instanceof JSplitPane) {
            JSplitPane splitPane = (JSplitPane) component;
            applyModernThemeRecursively(splitPane.getLeftComponent());
            applyModernThemeRecursively(splitPane.getRightComponent());
        }
    }

    private void applyThemeToComponent(Component component) {
        if (component instanceof JFrame) {
            component.setBackground(getBackgroundColor());
            component.setForeground(getForegroundColor());
        }
        else if (component instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) component;
            tabbedPane.setBackground(getPanelBackgroundColor());
            tabbedPane.setForeground(getForegroundColor());
        }
        else if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;

            // Check if it has a titled border
            if (panel.getBorder() instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder) panel.getBorder();
                titledBorder.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(getBorderColor(), 1),
                        BorderFactory.createEmptyBorder(10, 15, 15, 15)
                ));
                titledBorder.setTitleFont(getHeadingFont());
                titledBorder.setTitleColor(getForegroundColor());
                panel.setBackground(getCardBackgroundColor());
            } else if (panel.isOpaque()) {
                // Only change background if panel is opaque
                panel.setBackground(getCardBackgroundColor());
            }

            panel.setForeground(getForegroundColor());
        }
        else if (component instanceof JTextArea) {
            JTextArea textArea = (JTextArea) component;
            textArea.setBackground(getCardBackgroundColor());
            textArea.setForeground(getForegroundColor());
            textArea.setCaretColor(getForegroundColor());
        }
        else if (component instanceof JTextPane) {
            JTextPane textPane = (JTextPane) component;
            textPane.setBackground(getCardBackgroundColor());
            textPane.setForeground(getForegroundColor());
            textPane.setCaretColor(getForegroundColor());
        }
        else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setBackground(getCardBackgroundColor());
            textField.setForeground(getForegroundColor());
            textField.setCaretColor(getForegroundColor());
        }
        else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            // Only update if not already set to a specific color (like white for headers)
            Color currentColor = label.getForeground();
            if (currentColor == null ||
                    (!currentColor.equals(Color.WHITE) &&
                            !currentColor.equals(new Color(240, 240, 240)))) {
                label.setForeground(getForegroundColor());
            }
        }
        else if (component instanceof JList) {
            JList<?> list = (JList<?>) component;
            list.setBackground(getCardBackgroundColor());
            list.setForeground(getForegroundColor());
            list.setSelectionBackground(getAccentColor());
            list.setSelectionForeground(Color.WHITE);
        }
        else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setBackground(getCardBackgroundColor());
            comboBox.setForeground(getForegroundColor());
        }
        else if (component instanceof JButton) {
            // Buttons are styled individually, so we skip them here
            // to avoid overriding their specific styling
        }
        else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            scrollPane.setBackground(getCardBackgroundColor());
            scrollPane.getViewport().setBackground(getCardBackgroundColor());
        }
        else if (component instanceof JSplitPane) {
            JSplitPane splitPane = (JSplitPane) component;
            splitPane.setBackground(getPanelBackgroundColor());
        }
        else {
            // Generic component
            if (component.isOpaque() && component instanceof JComponent) {
                component.setBackground(getCardBackgroundColor());
                component.setForeground(getForegroundColor());
            }
        }
    }
}