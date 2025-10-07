package com.oratio.gui;

import com.oratio.models.Psalm;
import com.oratio.services.ThemeService;

import javax.swing.*;
import java.awt.*;

/**
 * Modern list cell renderer for psalms with clean design
 */
public class ModernPsalmListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        JPanel panel = new JPanel(new BorderLayout(10, 5));
        ThemeService themeService = ThemeService.getInstance();

        if (isSelected) {
            panel.setBackground(themeService.getAccentColor());
        } else {
            panel.setBackground(themeService.getCardBackgroundColor());
        }
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        if (value instanceof Psalm) {
            Psalm psalm = (Psalm) value;

            // Psalm number (prominent)
            JLabel numberLabel = new JLabel(String.valueOf(psalm.getNumber()));
            numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            numberLabel.setForeground(isSelected ? Color.WHITE : themeService.getAccentColor());
            numberLabel.setPreferredSize(new Dimension(40, 20));
            numberLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Psalm title/info
            JLabel titleLabel = new JLabel("Psalm " + psalm.getNumber());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(isSelected ? Color.WHITE : themeService.getForegroundColor());

            // Favorite indicator
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightPanel.setOpaque(false);
            if (psalm.isFavorite()) {
                JLabel favoriteIcon = new JLabel("");
                rightPanel.add(favoriteIcon);
            }

            panel.add(numberLabel, BorderLayout.WEST);
            panel.add(titleLabel, BorderLayout.CENTER);
            if (psalm.isFavorite()) {
                panel.add(rightPanel, BorderLayout.EAST);
            }
        }

        return panel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 50);
    }
}