package com.oratio.gui;

import com.oratio.models.RosaryMystery;
import com.oratio.models.RosaryStep;
import com.oratio.models.PrayerTimer;
import com.oratio.services.RosaryService;
import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RosaryPanel extends JPanel implements RosaryService.PrayerSessionListener {
    private JLabel mysteryLabel;
    private JLabel stepCounterLabel;
    private JTextArea stepTextArea;
    private JButton previousButton;
    private JButton nextButton;
    private JButton resetButton;
    private JComboBox<RosaryMystery> mysterySelector;

    // Timer components
    private JLabel timerLabel;
    private JButton pauseResumeButton;
    private JButton resetTimerButton;
    private Timer uiUpdateTimer;
    private boolean sessionActive = false;

    private RosaryService rosaryService;
    private LanguageService languageService;
    private List<RosaryStep> currentSteps;
    private int currentStepIndex = 0;

    public RosaryPanel() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        setupTimerUpdater();
        loadTodaysMystery();
    }

    private void initializeServices() {
        rosaryService = RosaryService.getInstance();
        languageService = LanguageService.getInstance();
        rosaryService.addSessionListener(this);
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeService.getInstance().getBackgroundColor());

        // Top panel for mystery selection and timer
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        topPanel.setPreferredSize(new Dimension(0, 140));

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

        // Timer panel
        JPanel timerPanel = createTimerPanel();

        topPanel.add(selectorPanel, BorderLayout.NORTH);
        topPanel.add(mysteryLabel, BorderLayout.CENTER);
        topPanel.add(timerPanel, BorderLayout.SOUTH);
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

    private JPanel createTimerPanel() {
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        timerPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());

        JLabel timerIconLabel = new JLabel("⏱ ");
        timerIconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timerIconLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        timerPanel.add(timerIconLabel);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timerLabel.setForeground(ThemeService.getInstance().getAccentColor());
        timerPanel.add(timerLabel);

        pauseResumeButton = new JButton("⏸ Pause");
        styleTimerButton(pauseResumeButton);
        timerPanel.add(pauseResumeButton);

        resetTimerButton = new JButton("↻ Reset Timer");
        styleTimerButton(resetTimerButton);
        timerPanel.add(resetTimerButton);

        return timerPanel;
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

    private void styleTimerButton(JButton button) {
        ThemeService theme = ThemeService.getInstance();
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(theme.getBorderColor());
        button.setForeground(theme.getForegroundColor());
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
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
            if (currentSteps != null) {
                if (currentStepIndex < currentSteps.size() - 1) {
                    currentStepIndex++;
                    displayCurrentStep();
                } else {
                    // Reached the end of rosary
                    endSession();
                }
            }
        });

        resetButton.addActionListener(e -> {
            currentStepIndex = 0;
            displayCurrentStep();
        });

        pauseResumeButton.addActionListener(e -> togglePauseResume());
        resetTimerButton.addActionListener(e -> resetTimer());
    }

    private void setupTimerUpdater() {
        // Update timer display every second
        uiUpdateTimer = new Timer(1000, e -> updateTimerDisplay());
        uiUpdateTimer.start();
    }

    private void updateTimerDisplay() {
        PrayerTimer timer = rosaryService.getCurrentTimer();
        timerLabel.setText(timer.getFormattedTime());

        // Update pause/resume button text
        if (timer.isRunning()) {
            pauseResumeButton.setText("⏸ Pause");
        } else if (timer.isPaused()) {
            pauseResumeButton.setText("▶ Resume");
        }
    }

    private void togglePauseResume() {
        PrayerTimer timer = rosaryService.getCurrentTimer();

        if (timer.isRunning()) {
            rosaryService.pausePrayerSession();
        } else {
            rosaryService.resumePrayerSession();
        }

        updateTimerDisplay();
    }

    private void resetTimer() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the timer?",
                "Reset Timer",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            rosaryService.resetPrayerTimer();
            updateTimerDisplay();
        }
    }

    private void endSession() {
        if (sessionActive) {
            rosaryService.endPrayerSession();
            sessionActive = false;
            updateTimerDisplay();
        }
    }

    @Override
    public void onSessionCompleted(String formattedDuration) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Rosary session completed!\n\nTotal prayer time: " + formattedDuration,
                    "Session Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
            // Timer stays paused at final time, showing the total duration
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

            // Start timer automatically when loading a mystery
            if (!sessionActive) {
                rosaryService.startPrayerSession();
                sessionActive = true;
            }
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

            // On last step, enable finish button
            if (currentStepIndex == currentSteps.size() - 1) {
                nextButton.setEnabled(true);
                nextButton.setText("Finish");
                styleButton(nextButton, true);
            } else {
                nextButton.setEnabled(true);
                nextButton.setText("Next");
                styleButton(nextButton, true);
            }
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

    // Clean up timer when panel is disposed
    public void dispose() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.stop();
        }
        rosaryService.removeSessionListener(this);
    }
}