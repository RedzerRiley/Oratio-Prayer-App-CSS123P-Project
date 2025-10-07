// File: src/main/java/com/oratio/gui/NovenaPanel.java
package com.oratio.gui;

import com.oratio.models.Novena;
import com.oratio.services.NovenaService;
import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;
import com.oratio.utils.ModernUIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Modern panel for displaying novenas
 */
public class NovenaPanel extends JPanel {
    private JList<Novena> novenaList;
    private JTextArea novenaTextArea;
    private JLabel novenaNameLabel;
    private JLabel novenaDescriptionLabel;
//    private JButton addToFavoritesButton;
//    private JButton startNovenaButton;

    private NovenaService novenaService;
    private LanguageService languageService;

    public NovenaPanel() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        loadNovenas();
    }

    private void initializeServices() {
        novenaService = NovenaService.getInstance();
        languageService = LanguageService.getInstance();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create modern left panel for novena list
        JPanel leftPanel = createModernCard("Available Novenas");
        leftPanel.setPreferredSize(new Dimension(300, 0));

        novenaList = new JList<>();
        novenaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        novenaList.setCellRenderer(new ModernNovenaListRenderer());
        ModernUIUtils.styleModernList(novenaList);

        JScrollPane listScrollPane = new JScrollPane(novenaList);
        listScrollPane.setBorder(null);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Create modern center panel for novena content
        JPanel centerPanel = createModernCard(null);

        // Modern header panel
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);

        novenaNameLabel = new JLabel();
        novenaNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        novenaNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        novenaNameLabel.setForeground(ThemeService.getInstance().getForegroundColor());

        novenaDescriptionLabel = new JLabel();
        novenaDescriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        novenaDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        novenaDescriptionLabel.setForeground(ThemeService.getInstance().getSecondaryTextColor());

        headerPanel.add(novenaNameLabel, BorderLayout.NORTH);
        headerPanel.add(novenaDescriptionLabel, BorderLayout.SOUTH);

        centerPanel.add(headerPanel, BorderLayout.NORTH);

        // Modern text area for novena prayers
        novenaTextArea = new JTextArea();
        novenaTextArea.setEditable(false);
        novenaTextArea.setWrapStyleWord(true);
        novenaTextArea.setLineWrap(true);
        ModernUIUtils.styleModernTextArea(novenaTextArea);

        JScrollPane textScrollPane = new JScrollPane(novenaTextArea);
        textScrollPane.setBorder(new ModernUIUtils.RoundedBorder(8,
                ThemeService.getInstance().getBorderColor()));
        textScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(textScrollPane, BorderLayout.CENTER);

        // Modern button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);

//        startNovenaButton = ModernUIUtils.createModernButton("Start Novena", true, "");
//        addToFavoritesButton = ModernUIUtils.createModernButton("Add to Favorites", false, "♡");
//
//        buttonPanel.add(startNovenaButton);
//        buttonPanel.add(addToFavoritesButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createModernCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        ThemeService themeService = ThemeService.getInstance();

        card.setBackground(themeService.getCardBackgroundColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                new ModernUIUtils.RoundedBorder(12, themeService.getBorderColor()),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        if (title != null) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(themeService.getHeadingFont());
            titleLabel.setForeground(themeService.getForegroundColor());
            card.add(titleLabel, BorderLayout.NORTH);
        }

        return card;
    }

    private void setupEventHandlers() {
        novenaList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Novena selectedNovena = novenaList.getSelectedValue();
                if (selectedNovena != null) {
                    displayNovena(selectedNovena);
                }
            }
        });

//        addToFavoritesButton.addActionListener(e -> {
//            Novena selectedNovena = novenaList.getSelectedValue();
//            if (selectedNovena != null) {
//                novenaService.addToFavorites(selectedNovena);
//                ModernUIUtils.showModernNotification(this, "Novena added to favorites",
//                        ModernUIUtils.NotificationType.SUCCESS);
//            }
//        });

//        startNovenaButton.addActionListener(e -> {
//            Novena selectedNovena = novenaList.getSelectedValue();
//            if (selectedNovena != null) {
//                novenaService.startNovena(selectedNovena);
//                ModernUIUtils.showModernNotification(this,
//                        "Novena started! Check your notes for daily progress",
//                        ModernUIUtils.NotificationType.SUCCESS);
//            }
//        });
    }

    private void loadNovenas() {
        List<Novena> novenas = novenaService.getAllNovenas();
        DefaultListModel<Novena> model = new DefaultListModel<>();
        for (Novena novena : novenas) {
            model.addElement(novena);
        }
        novenaList.setModel(model);

        if (!novenas.isEmpty()) {
            novenaList.setSelectedIndex(0);
        }
    }

    private void displayNovena(Novena novena) {
        String currentLanguage = languageService.getCurrentLanguage();

        novenaNameLabel.setText(novena.getName());
        novenaDescriptionLabel.setText(String.format("%s • %d days",
                novena.getDescription(), novena.getDurationDays()));

        String novenaText = novenaService.getNovenaText(novena, currentLanguage);
        novenaTextArea.setText(novenaText);
        novenaTextArea.setCaretPosition(0);
    }

    public void refreshContent() {
        Novena selectedNovena = novenaList.getSelectedValue();
        if (selectedNovena != null) {
            displayNovena(selectedNovena);
        }
    }

    /**
     * Modern cell renderer for novenas list
     */
    private static class ModernNovenaListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout());
            ThemeService themeService = ThemeService.getInstance();

            if (isSelected) {
                panel.setBackground(themeService.getAccentColor());
            } else {
                panel.setBackground(themeService.getCardBackgroundColor());
            }
            panel.setOpaque(true);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            if (value instanceof Novena) {
                Novena novena = (Novena) value;

                // Left: novena name
                JLabel nameLabel = new JLabel(novena.getName());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                nameLabel.setForeground(isSelected ? Color.WHITE : themeService.getForegroundColor());

                // Right: duration
                JLabel durationLabel = new JLabel(novena.getDurationDays() + " days");
                durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                durationLabel.setForeground(isSelected ?
                        new Color(255, 255, 255, 200) : themeService.getSecondaryTextColor());

                // Put them in one row
                JPanel rowPanel = new JPanel(new BorderLayout());
                rowPanel.setOpaque(false);
                rowPanel.add(nameLabel, BorderLayout.WEST);
                rowPanel.add(durationLabel, BorderLayout.EAST);

                panel.add(rowPanel, BorderLayout.CENTER);
            }

            return panel;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 40); // fits one line neatly
        }
    }


}