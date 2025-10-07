
// File: src/main/java/com/oratio/gui/RosaryPanel.java
package com.oratio.gui;

import com.oratio.models.RosaryMystery;
import com.oratio.models.RosaryStep;
import com.oratio.services.RosaryService;
import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RosaryPanel extends JPanel {
    private JLabel mysteryLabel;
    private JLabel stepCounterLabel;
    private JTextArea stepTextArea;
    private JButton previousButton;
    private JButton nextButton;
    private JButton resetButton;
    private JComboBox<RosaryMystery> mysterySelector;

    private RosaryService rosaryService;
    private LanguageService languageService;
    private List<RosaryStep> currentSteps;
    private int currentStepIndex = 0;

    public RosaryPanel() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        loadTodaysMystery();
    }

    private void initializeServices() {
        rosaryService = RosaryService.getInstance();
        languageService = LanguageService.getInstance();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeService.getInstance().getBackgroundColor());

        // Top panel for mystery selection
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        topPanel.setPreferredSize(new Dimension(0, 100));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());

        JLabel selectorLabel = new JLabel("Mystery: ");
        selectorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selectorLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        selectorPanel.add(selectorLabel);

        mysterySelector = new JComboBox<>(rosaryService.getAllMysteries().toArray(new RosaryMystery[0]));
        styleComboBox(mysterySelector);
        selectorPanel.add(mysterySelector);

        mysteryLabel = new JLabel();
        mysteryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mysteryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mysteryLabel.setForeground(ThemeService.getInstance().getForegroundColor());

        topPanel.add(selectorPanel, BorderLayout.NORTH);
        topPanel.add(mysteryLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for step content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel centerTitle = new JLabel("Prayer Guide");
        centerTitle.setFont(ThemeService.getInstance().getHeadingFont());
        centerTitle.setForeground(ThemeService.getInstance().getForegroundColor());
        centerPanel.add(centerTitle, BorderLayout.NORTH);

        stepCounterLabel = new JLabel();
        stepCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stepCounterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stepCounterLabel.setForeground(ThemeService.getInstance().getAccentColor());
        stepCounterLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel stepPanel = new JPanel(new BorderLayout());
        stepPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        stepPanel.add(stepCounterLabel, BorderLayout.NORTH);

        stepTextArea = new JTextArea();
        stepTextArea.setEditable(false);
        stepTextArea.setWrapStyleWord(true);
        stepTextArea.setLineWrap(true);
        stepTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stepTextArea.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        stepTextArea.setForeground(ThemeService.getInstance().getForegroundColor());
        stepTextArea.setCaretColor(ThemeService.getInstance().getForegroundColor());
        stepTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(stepTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()));
        stepPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(stepPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for navigation
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBackground(ThemeService.getInstance().getBackgroundColor());

        previousButton = new JButton("Previous");
        resetButton = new JButton("Reset");
        nextButton = new JButton("Next");

        styleButton(previousButton, false);
        styleButton(resetButton, false);
        styleButton(nextButton, true);

        bottomPanel.add(previousButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(nextButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, boolean isPrimary) {
        ThemeService theme = ThemeService.getInstance();
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        if (isPrimary) {
            button.setBackground(theme.getAccentColor());
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(theme.getBorderColor());
            button.setForeground(theme.getForegroundColor());
        }

        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        ThemeService theme = ThemeService.getInstance();
        comboBox.setBackground(theme.getCardBackgroundColor());
        comboBox.setForeground(theme.getForegroundColor());
        comboBox.setBorder(BorderFactory.createLineBorder(theme.getBorderColor()));
    }

    private void setupEventHandlers() {
        mysterySelector.addActionListener(e -> {
            RosaryMystery selectedMystery = (RosaryMystery) mysterySelector.getSelectedItem();
            if (selectedMystery != null) {
                loadMystery(selectedMystery);
            }
        });

        previousButton.addActionListener(e -> {
            if (currentStepIndex > 0) {
                currentStepIndex--;
                displayCurrentStep();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentSteps != null && currentStepIndex < currentSteps.size() - 1) {
                currentStepIndex++;
                displayCurrentStep();
            }
        });

        resetButton.addActionListener(e -> {
            currentStepIndex = 0;
            displayCurrentStep();
        });
    }

    private void loadTodaysMystery() {
        RosaryMystery todaysMystery = rosaryService.getTodaysMystery();
        mysterySelector.setSelectedItem(todaysMystery);
        loadMystery(todaysMystery);
    }

    private void loadMystery(RosaryMystery mystery) {
        if (mystery != null) {
            String currentLanguage = languageService.getCurrentLanguage();
            mysteryLabel.setText(rosaryService.getMysteryTitle(mystery, currentLanguage));
            currentSteps = rosaryService.getRosarySteps(mystery);
            currentStepIndex = 0;
            displayCurrentStep();
        }
    }

    private void displayCurrentStep() {
        if (currentSteps != null && !currentSteps.isEmpty() &&
                currentStepIndex >= 0 && currentStepIndex < currentSteps.size()) {

            RosaryStep step = currentSteps.get(currentStepIndex);
            String currentLanguage = languageService.getCurrentLanguage();

            stepCounterLabel.setText(String.format("Step %d of %d: %s",
                    currentStepIndex + 1, currentSteps.size(), step.getStepName()));

            String stepText = rosaryService.getStepText(step, currentLanguage);
            stepTextArea.setText(stepText);
            stepTextArea.setCaretPosition(0);

            previousButton.setEnabled(currentStepIndex > 0);
            nextButton.setEnabled(currentStepIndex < currentSteps.size() - 1);
        }
    }

    public void refreshContent() {
        // Apply current theme
        ThemeService theme = ThemeService.getInstance();
        setBackground(theme.getBackgroundColor());
        theme.applyTheme(this);

        RosaryMystery selectedMystery = (RosaryMystery) mysterySelector.getSelectedItem();
        if (selectedMystery != null) {
            loadMystery(selectedMystery);
        }
    }
}