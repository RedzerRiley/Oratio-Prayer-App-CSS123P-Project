package com.oratio.gui;

import com.oratio.models.Psalm;
import com.oratio.services.PsalmService;
import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;
import com.oratio.utils.ModernUIUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Modern panel for browsing and searching psalms with text highlighting support
 */
public class PsalmsPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JList<Psalm> psalmList;
    private JTextPane psalmTextPane; // Changed from JTextArea to JTextPane for highlighting
    private JLabel psalmTitleLabel;

    // Highlight controls
    private JButton highlightButton;
    private JButton chooseColorButton;
    private JButton clearHighlightsButton;
    private Color currentHighlightColor = Color.YELLOW;

    private PsalmService psalmService;
    private LanguageService languageService;
    private Psalm currentPsalm;

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

        // Right panel - modern psalm content with highlighting support
        JPanel rightPanel = createModernCard(null);

        psalmTitleLabel = new JLabel();
        psalmTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        psalmTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        psalmTitleLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        psalmTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Use JTextPane instead of JTextArea for advanced text styling
        psalmTextPane = new JTextPane();
        psalmTextPane.setEditable(false);
        psalmTextPane.setContentType("text/plain");
        styleTextPane();

        JScrollPane textScrollPane = new JScrollPane(psalmTextPane);
        textScrollPane.setBorder(new ModernUIUtils.RoundedBorder(8,
                ThemeService.getInstance().getBorderColor()));
        textScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Highlight controls panel
        JPanel highlightControlPanel = createHighlightControlPanel();

        rightPanel.add(psalmTitleLabel, BorderLayout.NORTH);
        rightPanel.add(textScrollPane, BorderLayout.CENTER);
        rightPanel.add(highlightControlPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Create the highlight control panel with buttons
     */
    private JPanel createHighlightControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        controlPanel.setOpaque(false);

        // Highlight selected text button
        highlightButton = ModernUIUtils.createModernButton("Highlight Selected", true, "");
        highlightButton.setToolTipText("Select text and click to highlight");

        // Choose color button
        chooseColorButton = ModernUIUtils.createModernButton("Choose Color", false, "");
        chooseColorButton.setToolTipText("Choose highlight color");
        updateColorButtonAppearance();

        // Clear all highlights button
        clearHighlightsButton = ModernUIUtils.createModernButton("Clear Highlights", false, "");
        clearHighlightsButton.setToolTipText("Remove all highlights from this psalm");

        controlPanel.add(highlightButton);
        controlPanel.add(chooseColorButton);
        controlPanel.add(clearHighlightsButton);

        return controlPanel;
    }

    /**
     * Style the JTextPane to match the theme
     */
    private void styleTextPane() {
        ThemeService theme = ThemeService.getInstance();

        psalmTextPane.setBackground(theme.getCardBackgroundColor());
        psalmTextPane.setForeground(theme.getForegroundColor());
        psalmTextPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        psalmTextPane.setMargin(new Insets(10, 10, 10, 10));

        // Set selection colors
        psalmTextPane.setSelectionColor(theme.getAccentColor());
        psalmTextPane.setSelectedTextColor(Color.WHITE);
        psalmTextPane.setCaretColor(theme.getForegroundColor());
    }

    /**
     * Update the color button to show the current highlight color
     */
    private void updateColorButtonAppearance() {
        chooseColorButton.setBackground(currentHighlightColor);

        // Set text color based on background brightness
        int brightness = (currentHighlightColor.getRed() +
                currentHighlightColor.getGreen() +
                currentHighlightColor.getBlue()) / 3;
        chooseColorButton.setForeground(brightness > 128 ? Color.BLACK : Color.WHITE);
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

        // Highlight button action
        highlightButton.addActionListener(e -> highlightSelectedText());

        // Choose color button action
        chooseColorButton.addActionListener(e -> chooseHighlightColor());

        // Clear highlights button action
        clearHighlightsButton.addActionListener(e -> clearAllHighlights());
    }

    /**
     * Highlight the currently selected text
     */
    private void highlightSelectedText() {
        if (currentPsalm == null) {
            ModernUIUtils.showModernNotification(this,
                    "Please select a psalm first",
                    ModernUIUtils.NotificationType.WARNING);
            return;
        }

        int start = psalmTextPane.getSelectionStart();
        int end = psalmTextPane.getSelectionEnd();

        if (start == end) {
            ModernUIUtils.showModernNotification(this,
                    "Please select text to highlight",
                    ModernUIUtils.NotificationType.INFO);
            return;
        }

        // Add highlight to service
        psalmService.addHighlight(currentPsalm.getNumber(), start, end, currentHighlightColor);

        // Apply highlight visually
        applyHighlightToText(start, end, currentHighlightColor);

        ModernUIUtils.showModernNotification(this,
                "Text highlighted successfully",
                ModernUIUtils.NotificationType.SUCCESS);

        // Clear selection
        psalmTextPane.setSelectionStart(start);
        psalmTextPane.setSelectionEnd(start);
    }

    /**
     * Open color chooser dialog to select highlight color
     */
    private void chooseHighlightColor() {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Highlight Color",
                currentHighlightColor
        );

        if (newColor != null) {
            currentHighlightColor = newColor;
            updateColorButtonAppearance();

            ModernUIUtils.showModernNotification(this,
                    "Highlight color updated",
                    ModernUIUtils.NotificationType.SUCCESS);
        }
    }

    /**
     * Clear all highlights from the current psalm
     */
    private void clearAllHighlights() {
        if (currentPsalm == null) {
            ModernUIUtils.showModernNotification(this,
                    "No psalm selected",
                    ModernUIUtils.NotificationType.WARNING);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all highlights from this psalm?",
                "Clear Highlights",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            psalmService.clearAllHighlights(currentPsalm.getNumber());
            displayPsalm(currentPsalm); // Refresh display

            ModernUIUtils.showModernNotification(this,
                    "All highlights cleared",
                    ModernUIUtils.NotificationType.SUCCESS);
        }
    }

    /**
     * Apply a single highlight to the text pane
     */
    private void applyHighlightToText(int start, int end, Color color) {
        StyledDocument doc = psalmTextPane.getStyledDocument();
        SimpleAttributeSet highlightStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(highlightStyle, color);

        doc.setCharacterAttributes(start, end - start, highlightStyle, false);
    }

    /**
     * Apply all saved highlights to the current psalm
     */
    private void applyAllHighlights() {
        if (currentPsalm == null) {
            return;
        }

        // First, clear all existing highlights
        StyledDocument doc = psalmTextPane.getStyledDocument();
        SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(defaultStyle, psalmTextPane.getBackground());
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, false);

        // Get saved highlights from service
        List<PsalmService.HighlightData> highlights =
                psalmService.getHighlights(currentPsalm.getNumber());

        // Apply each highlight
        for (PsalmService.HighlightData highlight : highlights) {
            try {
                int start = highlight.getStartIndex();
                int end = highlight.getEndIndex();
                Color color = highlight.getColor();

                // Validate indices
                if (start >= 0 && end <= doc.getLength() && start < end) {
                    applyHighlightToText(start, end, color);
                }
            } catch (Exception e) {
                System.err.println("Error applying highlight: " + e.getMessage());
            }
        }
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
        currentPsalm = psalm;

        String currentLanguage = languageService.getCurrentLanguage();
        String title = psalmService.getPsalmTitle(psalm, currentLanguage);
        String text = psalmService.getPsalmText(psalm, currentLanguage);

        psalmTitleLabel.setText(title);
        psalmTextPane.setText(text);
        psalmTextPane.setCaretPosition(0);

        // Apply saved highlights
        applyAllHighlights();

        // Update clear button state
        boolean hasHighlights = psalmService.hasHighlights(psalm.getNumber());
        clearHighlightsButton.setEnabled(hasHighlights);
    }

    public void refreshContent() {
        Psalm selectedPsalm = psalmList.getSelectedValue();
        if (selectedPsalm != null) {
            displayPsalm(selectedPsalm);
        }
    }

    /**
     * Refresh theme when theme changes
     */
    public void refreshTheme() {
        styleTextPane();
        if (currentPsalm != null) {
            displayPsalm(currentPsalm);
        }
    }
}