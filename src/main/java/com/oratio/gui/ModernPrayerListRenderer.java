// File: src/main/java/com/oratio/gui/renderers/ModernPrayerListRenderer.java
package com.oratio.gui;

import com.oratio.models.Prayer;
import com.oratio.services.ThemeService;

import javax.swing.*;
import java.awt.*;

/**
 * Modern list cell renderer for prayers with card-style design
 */
public class ModernPrayerListRenderer extends DefaultListCellRenderer {

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
                JLabel favoriteIcon = new JLabel("⭐");
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
