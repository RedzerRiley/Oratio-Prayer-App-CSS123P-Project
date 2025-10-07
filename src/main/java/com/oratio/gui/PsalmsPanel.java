package com.oratio.gui;

import com.oratio.models.Psalm;
import com.oratio.services.PsalmService;
import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;
import com.oratio.utils.ModernUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Modern panel for browsing and searching psalms
 */
public class PsalmsPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JList<Psalm> psalmList;
    private JTextArea psalmTextArea;
//    private JButton addToFavoritesButton;
    private JLabel psalmTitleLabel;

    private PsalmService psalmService;
    private LanguageService languageService;

    public PsalmsPanel() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        loadAllPsalms();
    }

    private void initializeServices() {
        psalmService = PsalmService.getInstance();
        languageService = LanguageService.getInstance();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create modern search panel
        JPanel searchPanel = createModernCard("Search Psalms");
        searchPanel.setPreferredSize(new Dimension(0, 149));

        JLabel instructionLabel = new JLabel("Enter psalm number or keywords:");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(ThemeService.getInstance().getForegroundColor());

        JPanel searchInputPanel = new JPanel(new BorderLayout(10, 10));
        searchInputPanel.setOpaque(false);

        searchField = ModernUIUtils.createModernTextField("Search psalms...");
        searchButton = ModernUIUtils.createModernButton("Search", true, "");

        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchInputPanel.add(searchButton, BorderLayout.EAST);

        JPanel searchContent = new JPanel(new BorderLayout(0, 10));
        searchContent.setOpaque(false);
        searchContent.add(instructionLabel, BorderLayout.NORTH);
        searchContent.add(searchInputPanel, BorderLayout.CENTER);

        searchPanel.add(searchContent, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // Create modern main content panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left panel - modern psalm list
        JPanel leftPanel = createModernCard("Psalms");
        leftPanel.setPreferredSize(new Dimension(250, 10));

        psalmList = new JList<>();
        psalmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        psalmList.setCellRenderer(new ModernPsalmListRenderer());
        ModernUIUtils.styleModernList(psalmList);

        JScrollPane listScrollPane = new JScrollPane(psalmList);
        listScrollPane.setBorder(null);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // Right panel - modern psalm content
        JPanel rightPanel = createModernCard(null);

        psalmTitleLabel = new JLabel();
        psalmTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        psalmTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        psalmTitleLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        psalmTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        psalmTextArea = new JTextArea();
        psalmTextArea.setEditable(false);
        psalmTextArea.setWrapStyleWord(true);
        psalmTextArea.setLineWrap(true);
        ModernUIUtils.styleModernTextArea(psalmTextArea);

        JScrollPane textScrollPane = new JScrollPane(psalmTextArea);
        textScrollPane.setBorder(new ModernUIUtils.RoundedBorder(8,
                ThemeService.getInstance().getBorderColor()));
        textScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setOpaque(false);
//        addToFavoritesButton = ModernUIUtils.createModernButton("Add to Favorites", true, "♡");
//        buttonPanel.add(addToFavoritesButton);

        rightPanel.add(psalmTitleLabel, BorderLayout.NORTH);
        rightPanel.add(textScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createModernCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        ThemeService themeService = ThemeService.getInstance();

        card.setBackground(themeService.getCardBackgroundColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                new ModernUIUtils.RoundedBorder(12, themeService.getBorderColor()),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
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
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        searchButton.addActionListener(e -> performSearch());

        psalmList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Psalm selectedPsalm = psalmList.getSelectedValue();
                if (selectedPsalm != null) {
                    displayPsalm(selectedPsalm);
                }
            }
        });

//        addToFavoritesButton.addActionListener(e -> {
//            Psalm selectedPsalm = psalmList.getSelectedValue();
//            if (selectedPsalm != null) {
//                psalmService.addToFavorites(selectedPsalm);
//                ModernUIUtils.showModernNotification(this, "Psalm added to favorites",
//                        ModernUIUtils.NotificationType.SUCCESS);
//            }
//        });
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        List<Psalm> searchResults;

        if (searchText.isEmpty() || searchText.equals("Search psalms...")) {
            searchResults = psalmService.getAllPsalms();
        } else {
            try {
                int psalmNumber = Integer.parseInt(searchText);
                Psalm psalm = psalmService.getPsalmByNumber(psalmNumber);
                searchResults = psalm != null ? List.of(psalm) : List.of();
            } catch (NumberFormatException e) {
                searchResults = psalmService.searchPsalmsByKeyword(searchText);
            }
        }

        updatePsalmList(searchResults);
    }

    private void loadAllPsalms() {
        List<Psalm> allPsalms = psalmService.getAllPsalms();
        updatePsalmList(allPsalms);
    }

    private void updatePsalmList(List<Psalm> psalms) {
        DefaultListModel<Psalm> model = new DefaultListModel<>();
        for (Psalm psalm : psalms) {
            model.addElement(psalm);
        }
        psalmList.setModel(model);

        if (!psalms.isEmpty()) {
            psalmList.setSelectedIndex(0);
        }
    }

    private void displayPsalm(Psalm psalm) {
        String currentLanguage = languageService.getCurrentLanguage();
        String title = psalmService.getPsalmTitle(psalm, currentLanguage);
        String text = psalmService.getPsalmText(psalm, currentLanguage);

        psalmTitleLabel.setText(title);
        psalmTextArea.setText(text);
        psalmTextArea.setCaretPosition(0);
    }

    public void refreshContent() {
        Psalm selectedPsalm = psalmList.getSelectedValue();
        if (selectedPsalm != null) {
            displayPsalm(selectedPsalm);
        }
    }

}