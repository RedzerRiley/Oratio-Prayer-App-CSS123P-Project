//// File: src/main/java/com/oratio/gui/FavoritesPanel.java
//package com.oratio.gui;
//
//import com.oratio.models.*;
//import com.oratio.services.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.List;
//
//public class FavoritesPanel extends JPanel {
//    private JTabbedPane favoritesTabPane;
//    private JList<Prayer> favoritePrayersList;
//    private JList<Psalm> favoritePsalmsList;
//    private JList<Novena> favoriteNovenasList;
//    private JTextArea contentArea;
//    private JLabel contentTitleLabel;
//    private JButton removeButton;
//
//    private PrayerService prayerService;
//    private PsalmService psalmService;
//    private NovenaService novenaService;
//    private LanguageService languageService;
//
//    public FavoritesPanel() {
//        initializeServices();
//        initializeGUI();
//        setupEventHandlers();
//        loadFavorites();
//    }
//
//    private void initializeServices() {
//        prayerService = PrayerService.getInstance();
//        psalmService = PsalmService.getInstance();
//        novenaService = NovenaService.getInstance();
//        languageService = LanguageService.getInstance();
//    }
//
//    private void initializeGUI() {
//        setLayout(new BorderLayout(10, 10));
//        setBackground(ThemeService.getInstance().getBackgroundColor());
//
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        splitPane.setResizeWeight(0.35);
//
//        // Left panel with tabs
//        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
//        leftPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
//        leftPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
//                BorderFactory.createEmptyBorder(15, 15, 15, 15)
//        ));
//        leftPanel.setPreferredSize(new Dimension(320, 0));
//        leftPanel.setMinimumSize(new Dimension(250, 0));
//
//        JLabel leftTitle = new JLabel("Favorites");
//        leftTitle.setFont(ThemeService.getInstance().getHeadingFont());
//        leftTitle.setForeground(ThemeService.getInstance().getForegroundColor());
//        leftPanel.add(leftTitle, BorderLayout.NORTH);
//
//        favoritesTabPane = new JTabbedPane();
//        ThemeService theme = ThemeService.getInstance();
//        favoritesTabPane.setBackground(theme.getCardBackgroundColor());
//        favoritesTabPane.setForeground(theme.getForegroundColor());
//
//        // Prayers tab
//        favoritePrayersList = new JList<>();
//        favoritePrayersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        favoritePrayersList.setCellRenderer(new FavoritePrayerRenderer());
//        styleList(favoritePrayersList);
//
//        JScrollPane prayersScrollPane = new JScrollPane(favoritePrayersList);
//        prayersScrollPane.setBorder(null);
//        favoritesTabPane.addTab("Prayers", prayersScrollPane);
//
//        // Psalms tab
//        favoritePsalmsList = new JList<>();
//        favoritePsalmsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        favoritePsalmsList.setCellRenderer(new FavoritePsalmRenderer());
//        styleList(favoritePsalmsList);
//
//        JScrollPane psalmsScrollPane = new JScrollPane(favoritePsalmsList);
//        psalmsScrollPane.setBorder(null);
//        favoritesTabPane.addTab("Psalms", psalmsScrollPane);
//
//        // Novenas tab
//        favoriteNovenasList = new JList<>();
//        favoriteNovenasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        favoriteNovenasList.setCellRenderer(new FavoriteNovenaRenderer());
//        styleList(favoriteNovenasList);
//
//        JScrollPane novenasScrollPane = new JScrollPane(favoriteNovenasList);
//        novenasScrollPane.setBorder(null);
//        favoritesTabPane.addTab("Novenas", novenasScrollPane);
//
//        leftPanel.add(favoritesTabPane, BorderLayout.CENTER);
//        splitPane.setLeftComponent(leftPanel);
//
//        // Right panel for content
//        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
//        rightPanel.setBackground(theme.getCardBackgroundColor());
//        rightPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(theme.getBorderColor()),
//                BorderFactory.createEmptyBorder(15, 15, 15, 15)
//        ));
//
//        contentTitleLabel = new JLabel("Select a favorite to view");
//        contentTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        contentTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        contentTitleLabel.setForeground(theme.getForegroundColor());
//        rightPanel.add(contentTitleLabel, BorderLayout.NORTH);
//
//        contentArea = new JTextArea();
//        contentArea.setEditable(false);
//        contentArea.setWrapStyleWord(true);
//        contentArea.setLineWrap(true);
//        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        contentArea.setBackground(theme.getCardBackgroundColor());
//        contentArea.setForeground(theme.getForegroundColor());
//        contentArea.setCaretColor(theme.getForegroundColor());
//
//        JScrollPane contentScrollPane = new JScrollPane(contentArea);
//        contentScrollPane.setBorder(BorderFactory.createLineBorder(theme.getBorderColor()));
//        rightPanel.add(contentScrollPane, BorderLayout.CENTER);
//
//        // Remove button
//        JPanel buttonPanel = new JPanel(new FlowLayout());
//        buttonPanel.setBackground(theme.getCardBackgroundColor());
//        removeButton = new JButton("Remove from Favorites");
//        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
//        removeButton.setBackground(new Color(220, 53, 69));
//        removeButton.setForeground(Color.WHITE);
//        removeButton.setBorderPainted(false);
//        removeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//        buttonPanel.add(removeButton);
//        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        splitPane.setRightComponent(rightPanel);
//        splitPane.setDividerLocation(350);
//
//        add(splitPane, BorderLayout.CENTER);
//    }
//
//    private void styleList(JList<?> list) {
//        ThemeService theme = ThemeService.getInstance();
//        list.setBackground(theme.getCardBackgroundColor());
//        list.setForeground(theme.getForegroundColor());
//        list.setSelectionBackground(theme.getAccentColor());
//        list.setSelectionForeground(Color.WHITE);
//        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//    }
//
//    private void setupEventHandlers() {
//        favoritePrayersList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                Prayer selected = favoritePrayersList.getSelectedValue();
//                if (selected != null) {
//                    clearOtherSelections(1);
//                    displayPrayerContent(selected);
//                }
//            }
//        });
//
//        favoritePsalmsList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                Psalm selected = favoritePsalmsList.getSelectedValue();
//                if (selected != null) {
//                    clearOtherSelections(2);
//                    displayPsalmContent(selected);
//                }
//            }
//        });
//
//        favoriteNovenasList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                Novena selected = favoriteNovenasList.getSelectedValue();
//                if (selected != null) {
//                    clearOtherSelections(3);
//                    displayNovenaContent(selected);
//                }
//            }
//        });
//
//        removeButton.addActionListener(e -> removeSelectedFavorite());
//    }
//
//    private void clearOtherSelections(int exceptTab) {
//        if (exceptTab != 1) favoritePrayersList.clearSelection();
//        if (exceptTab != 2) favoritePsalmsList.clearSelection();
//        if (exceptTab != 3) favoriteNovenasList.clearSelection();
//    }
//
//    private void loadFavorites() {
//        // Load favorite prayers
//        List<Prayer> favoritePrayers = prayerService.getFavoritePrayers();
//        DefaultListModel<Prayer> prayersModel = new DefaultListModel<>();
//        for (Prayer prayer : favoritePrayers) {
//            prayersModel.addElement(prayer);
//        }
//        favoritePrayersList.setModel(prayersModel);
//
//        // Load favorite psalms
//        List<Psalm> favoritePsalms = psalmService.getFavoritePsalms();
//        DefaultListModel<Psalm> psalmsModel = new DefaultListModel<>();
//        for (Psalm psalm : favoritePsalms) {
//            psalmsModel.addElement(psalm);
//        }
//        favoritePsalmsList.setModel(psalmsModel);
//
//        // Load favorite novenas
//        List<Novena> favoriteNovenas = novenaService.getFavoriteNovenas();
//        DefaultListModel<Novena> novenasModel = new DefaultListModel<>();
//        for (Novena novena : favoriteNovenas) {
//            novenasModel.addElement(novena);
//        }
//        favoriteNovenasList.setModel(novenasModel);
//    }
//
//    private void displayPrayerContent(Prayer prayer) {
//        contentTitleLabel.setText(prayer.getName());
//        String content = prayerService.getPrayerText(prayer, languageService.getCurrentLanguage());
//        contentArea.setText(content);
//        contentArea.setCaretPosition(0);
//    }
//
//    private void displayPsalmContent(Psalm psalm) {
//        String title = psalmService.getPsalmTitle(psalm, languageService.getCurrentLanguage());
//        contentTitleLabel.setText(title);
//        String content = psalmService.getPsalmText(psalm, languageService.getCurrentLanguage());
//        contentArea.setText(content);
//        contentArea.setCaretPosition(0);
//    }
//
//    private void displayNovenaContent(Novena novena) {
//        contentTitleLabel.setText(novena.getName());
//        String content = novenaService.getNovenaText(novena, languageService.getCurrentLanguage());
//        contentArea.setText(novena.getDescription() + "\n\n" + content);
//        contentArea.setCaretPosition(0);
//    }
//
//    private void removeSelectedFavorite() {
//        int selectedTab = favoritesTabPane.getSelectedIndex();
//
//        switch (selectedTab) {
//            case 0: // Prayers
//                Prayer selectedPrayer = favoritePrayersList.getSelectedValue();
//                if (selectedPrayer != null) {
//                    prayerService.removeFromFavorites(selectedPrayer);
//                    loadFavorites();
//                    contentArea.setText("");
//                    contentTitleLabel.setText("Select a favorite to view");
//                    JOptionPane.showMessageDialog(this, "Prayer removed from favorites!");
//                }
//                break;
//            case 1: // Psalms
//                Psalm selectedPsalm = favoritePsalmsList.getSelectedValue();
//                if (selectedPsalm != null) {
//                    psalmService.removeFromFavorites(selectedPsalm);
//                    loadFavorites();
//                    contentArea.setText("");
//                    contentTitleLabel.setText("Select a favorite to view");
//                    JOptionPane.showMessageDialog(this, "Psalm removed from favorites!");
//                }
//                break;
//            case 2: // Novenas
//                Novena selectedNovena = favoriteNovenasList.getSelectedValue();
//                if (selectedNovena != null) {
//                    novenaService.removeFromFavorites(selectedNovena);
//                    loadFavorites();
//                    contentArea.setText("");
//                    contentTitleLabel.setText("Select a favorite to view");
//                    JOptionPane.showMessageDialog(this, "Novena removed from favorites!");
//                }
//                break;
//        }
//    }
//
//    public void refreshContent() {
//        // Apply current theme
//        ThemeService theme = ThemeService.getInstance();
//        setBackground(theme.getBackgroundColor());
//        theme.applyTheme(this);
//
//        loadFavorites();
//    }
//
//    // Renderers for the lists
//    private static class FavoritePrayerRenderer extends DefaultListCellRenderer {
//        @Override
//        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
//                                                      boolean isSelected, boolean cellHasFocus) {
//            JPanel panel = new JPanel(new BorderLayout(8, 4));
//            ThemeService theme = ThemeService.getInstance();
//
//            if (isSelected) {
//                panel.setBackground(theme.getAccentColor());
//            } else {
//                panel.setBackground(theme.getCardBackgroundColor());
//            }
//            panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
//
//            if (value instanceof Prayer) {
//                Prayer prayer = (Prayer) value;
//
//                JLabel nameLabel = new JLabel(prayer.getName());
//                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
//                nameLabel.setForeground(isSelected ? Color.WHITE : theme.getForegroundColor());
//
//                JLabel categoryLabel = new JLabel(prayer.getCategory());
//                categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
//                categoryLabel.setForeground(isSelected ?
//                        new Color(255, 255, 255, 180) : theme.getSecondaryTextColor());
//
//                panel.add(nameLabel, BorderLayout.NORTH);
//                panel.add(categoryLabel, BorderLayout.SOUTH);
//            }
//
//            return panel;
//        }
//
//        @Override
//        public Dimension getPreferredSize() {
//            return new Dimension(super.getPreferredSize().width, 45);
//        }
//    }
//
//    private static class FavoritePsalmRenderer extends DefaultListCellRenderer {
//        @Override
//        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
//                                                      boolean isSelected, boolean cellHasFocus) {
//            JPanel panel = new JPanel(new BorderLayout(8, 4));
//            ThemeService theme = ThemeService.getInstance();
//
//            if (isSelected) {
//                panel.setBackground(theme.getAccentColor());
//            } else {
//                panel.setBackground(theme.getCardBackgroundColor());
//            }
//            panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
//
//            if (value instanceof Psalm) {
//                Psalm psalm = (Psalm) value;
//
//                JLabel numberLabel = new JLabel(String.valueOf(psalm.getNumber()));
//                numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
//                numberLabel.setForeground(isSelected ? Color.WHITE : theme.getAccentColor());
//                numberLabel.setPreferredSize(new Dimension(30, 20));
//
//                JLabel titleLabel = new JLabel("Psalm " + psalm.getNumber());
//                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
//                titleLabel.setForeground(isSelected ? Color.WHITE : theme.getForegroundColor());
//
//                panel.add(numberLabel, BorderLayout.WEST);
//                panel.add(titleLabel, BorderLayout.CENTER);
//            }
//
//            return panel;
//        }
//
//        @Override
//        public Dimension getPreferredSize() {
//            return new Dimension(super.getPreferredSize().width, 40);
//        }
//    }
//
//    private static class FavoriteNovenaRenderer extends DefaultListCellRenderer {
//        @Override
//        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
//                                                      boolean isSelected, boolean cellHasFocus) {
//            JPanel panel = new JPanel(new BorderLayout(8, 4));
//            ThemeService theme = ThemeService.getInstance();
//
//            if (isSelected) {
//                panel.setBackground(theme.getAccentColor());
//            } else {
//                panel.setBackground(theme.getCardBackgroundColor());
//            }
//            panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
//
//            if (value instanceof Novena) {
//                Novena novena = (Novena) value;
//
//                JLabel nameLabel = new JLabel(novena.getName());
//                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
//                nameLabel.setForeground(isSelected ? Color.WHITE : theme.getForegroundColor());
//
//                JLabel durationLabel = new JLabel(novena.getDurationDays() + " days");
//                durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
//                durationLabel.setForeground(isSelected ?
//                        new Color(255, 255, 255, 180) : theme.getSecondaryTextColor());
//
//                panel.add(nameLabel, BorderLayout.NORTH);
//                panel.add(durationLabel, BorderLayout.SOUTH);
//            }
//
//            return panel;
//        }
//
//        @Override
//        public Dimension getPreferredSize() {
//            return new Dimension(super.getPreferredSize().width, 45);
//        }
//    }
//}