// File: src/main/java/com/oratio/gui/panels/PrayersPanel.java
package com.oratio.gui;

import com.oratio.models.Prayer;
import com.oratio.services.PrayerService;
import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for displaying daily prayers and devotions
 */
public class PrayersPanel extends JPanel {
    private JList<Prayer> prayerList;
    private JTextArea prayerTextArea;
    private JButton markCompletedButton;
//    private JButton addToFavoritesButton;
    private JScrollPane textScrollPane;

    private PrayerService prayerService;
    private LanguageService languageService;

    public PrayersPanel() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        loadPrayers();
    }

    private void initializeServices() {
        prayerService = PrayerService.getInstance();
        languageService = LanguageService.getInstance();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create modern left panel for prayer list
        JPanel leftPanel = createModernCard("Daily Prayers");
        leftPanel.setPreferredSize(new Dimension(280, 0));

        prayerList = new JList<>();
        prayerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prayerList.setCellRenderer(new ModernPrayerListRenderer());

        JScrollPane listScrollPane = new JScrollPane(prayerList);
        listScrollPane.setBorder(null);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Create modern center panel for prayer content
        JPanel centerPanel = createModernCard("Prayer Text");

        prayerTextArea = new JTextArea();
        prayerTextArea.setEditable(false);
        prayerTextArea.setWrapStyleWord(true);
        prayerTextArea.setLineWrap(true);
        prayerTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prayerTextArea.setMargin(new Insets(20, 20, 20, 20));

        textScrollPane = new JScrollPane(prayerTextArea);
        textScrollPane.setBorder(null);
        textScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(textScrollPane, BorderLayout.CENTER);

        // Create modern button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);

        markCompletedButton = createModernButton(" Mark Completed", true);
//        addToFavoritesButton = createModernButton(" Add to Favorites", false);

        buttonPanel.add(markCompletedButton);
//        buttonPanel.add(addToFavoritesButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createModernCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));

        // Modern card styling
        ThemeService themeService = ThemeService.getInstance();
        card.setBackground(themeService.getCardBackgroundColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeService.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Modern title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(themeService.getHeadingFont());
        titleLabel.setForeground(themeService.getForegroundColor());
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JButton createModernButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        ThemeService themeService = ThemeService.getInstance();

        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setBackground(themeService.getAccentColor());
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(themeService.getBorderColor());
            button.setForeground(themeService.getForegroundColor());
        }

        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add modern hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalBg = button.getBackground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(new Color(64, 75, 152));
                } else {
                    button.setBackground(themeService.getSecondaryTextColor());
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
                if (!isPrimary) {
                    button.setForeground(themeService.getForegroundColor());
                }
            }
        });

        return button;
    }

    private void setupEventHandlers() {
        prayerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Prayer selectedPrayer = prayerList.getSelectedValue();
                if (selectedPrayer != null) {
                    displayPrayer(selectedPrayer);
                }
            }
        });

        markCompletedButton.addActionListener(e -> {
            Prayer selectedPrayer = prayerList.getSelectedValue();
            if (selectedPrayer != null) {
                prayerService.markAsCompleted(selectedPrayer);
                JOptionPane.showMessageDialog(this, "Prayer marked as completed!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

//        addToFavoritesButton.addActionListener(e -> {
//            Prayer selectedPrayer = prayerList.getSelectedValue();
//            if (selectedPrayer != null) {
//                prayerService.addToFavorites(selectedPrayer);
//                JOptionPane.showMessageDialog(this, "Prayer added to favorites!",
//                        "Success", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
    }

    private void loadPrayers() {
        List<Prayer> prayers = prayerService.getDailyPrayers();
        DefaultListModel<Prayer> model = new DefaultListModel<>();
        for (Prayer prayer : prayers) {
            model.addElement(prayer);
        }
        prayerList.setModel(model);

        // Select first prayer by default
        if (!prayers.isEmpty()) {
            prayerList.setSelectedIndex(0);
        }
    }

    private void displayPrayer(Prayer prayer) {
        String currentLanguage = languageService.getCurrentLanguage();
        String prayerText = prayerService.getPrayerText(prayer, currentLanguage);
        prayerTextArea.setText(prayerText);
        prayerTextArea.setCaretPosition(0);
    }

    public void refreshContent() {
        Prayer selectedPrayer = prayerList.getSelectedValue();
        loadPrayers();
        if (selectedPrayer != null) {
            displayPrayer(selectedPrayer);
        }
    }

    /**
     * Modern list cell renderer for prayers
     */
    private static class ModernPrayerListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout(10, 5));
            ThemeService themeService = ThemeService.getInstance();

            if (isSelected) {
                panel.setBackground(themeService.getAccentColor());
                panel.setOpaque(true);
            } else {
                    panel.setBackground(themeService.getCardBackgroundColor());
                panel.setOpaque(true);
            }

            panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

            if (value instanceof Prayer) {
                Prayer prayer = (Prayer) value;

                // Prayer name
                JLabel nameLabel = new JLabel(prayer.getName());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                nameLabel.setForeground(isSelected ? Color.WHITE : themeService.getForegroundColor());

                // Prayer category
                JLabel categoryLabel = new JLabel(prayer.getCategory());
                categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                categoryLabel.setForeground(isSelected ?
                        new Color(255, 255, 255, 180) : themeService.getSecondaryTextColor());

                // Status indicators
                JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                statusPanel.setOpaque(false);

                if (prayer.isCompleted()) {
                    JLabel completedIcon = new JLabel("✓");
                    completedIcon.setForeground(isSelected ? Color.WHITE : new Color(40, 167, 69));
                    statusPanel.add(completedIcon);
                }

                if (prayer.isFavorite()) {
                    JLabel favoriteIcon = new JLabel("");
                    statusPanel.add(favoriteIcon);
                }

                JPanel textPanel = new JPanel(new BorderLayout());
                textPanel.setOpaque(false);
                textPanel.add(nameLabel, BorderLayout.NORTH);
                textPanel.add(categoryLabel, BorderLayout.SOUTH);

                panel.add(textPanel, BorderLayout.CENTER);
                if (statusPanel.getComponentCount() > 0) {
                    panel.add(statusPanel, BorderLayout.EAST);
                }
            }

            return panel;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 55);
        }
    }
}