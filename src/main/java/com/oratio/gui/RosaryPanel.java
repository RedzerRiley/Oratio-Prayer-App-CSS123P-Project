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
    private JLabel beadImageLabel;
    private JLabel todayLabel;

    // Timer components
    private JLabel timerLabel;
    private JButton startButton;
    private JButton pauseResumeButton;
    private JButton resetTimerButton;
    private Timer uiUpdateTimer;
    private boolean sessionActive = false;

    private RosaryService rosaryService;
    private LanguageService languageService;
    private List<RosaryStep> currentSteps;
    private int currentStepIndex = 0;
    private RosaryMystery currentMystery;

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
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeService.getInstance().getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left panel for bead visualization
        JPanel leftPanel = createBeadVisualizationPanel();
        add(leftPanel, BorderLayout.WEST);

        // Center panel for main content
        JPanel centerPanel = createMainContentPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for navigation
        JPanel bottomPanel = createNavigationPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createBeadVisualizationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor(), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(280, 0));

        // Title for bead panel
        JLabel beadPanelTitle = new JLabel("Current Bead");
        beadPanelTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        beadPanelTitle.setForeground(ThemeService.getInstance().getForegroundColor());
        beadPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(beadPanelTitle, BorderLayout.NORTH);

        // Image placeholder for rosary bead
        beadImageLabel = new JLabel();
        beadImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        beadImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        beadImageLabel.setPreferredSize(new Dimension(240, 240));
        beadImageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        beadImageLabel.setOpaque(true);
        beadImageLabel.setBackground(ThemeService.getInstance().getBackgroundColor());

        // Placeholder text
        JPanel imagePlaceholder = new JPanel(new BorderLayout());
        imagePlaceholder.setBackground(ThemeService.getInstance().getBackgroundColor());
        JLabel placeholderText = new JLabel("<html><center>Bead Image<br/>Placeholder<br/><br/><small>Add your image here</small></center></html>");
        placeholderText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        placeholderText.setForeground(ThemeService.getInstance().getForegroundColor());
        placeholderText.setHorizontalAlignment(SwingConstants.CENTER);
        imagePlaceholder.add(placeholderText, BorderLayout.CENTER);
        beadImageLabel.setLayout(new BorderLayout());
        beadImageLabel.add(imagePlaceholder, BorderLayout.CENTER);

        panel.add(beadImageLabel, BorderLayout.CENTER);

        // Info text
        JLabel infoLabel = new JLabel("<html><center><small>Visual guide for<br/>current prayer step</small></center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(infoLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeService.getInstance().getBackgroundColor());

        // Top section with mystery and timer
        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        topSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor(), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Today's mystery header
        JPanel mysteryHeaderPanel = new JPanel(new BorderLayout(10, 5));
        mysteryHeaderPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());

        todayLabel = new JLabel("Today's Mystery");
        todayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        todayLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        mysteryHeaderPanel.add(todayLabel, BorderLayout.NORTH);

        mysteryLabel = new JLabel();
        mysteryLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mysteryLabel.setForeground(ThemeService.getInstance().getAccentColor());
        mysteryHeaderPanel.add(mysteryLabel, BorderLayout.CENTER);

        topSection.add(mysteryHeaderPanel, BorderLayout.NORTH);

        // Timer panel
        JPanel timerPanel = createTimerPanel();
        topSection.add(timerPanel, BorderLayout.CENTER);

        panel.add(topSection, BorderLayout.NORTH);

        // Prayer content section
        JPanel contentSection = new JPanel(new BorderLayout(10, 10));
        contentSection.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        contentSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor(), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Step counter
        stepCounterLabel = new JLabel();
        stepCounterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stepCounterLabel.setForeground(ThemeService.getInstance().getAccentColor());
        stepCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stepCounterLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentSection.add(stepCounterLabel, BorderLayout.NORTH);

        // Prayer text area
        stepTextArea = new JTextArea();
        stepTextArea.setEditable(false);
        stepTextArea.setWrapStyleWord(true);
        stepTextArea.setLineWrap(true);
        stepTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        stepTextArea.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        stepTextArea.setForeground(ThemeService.getInstance().getForegroundColor());
        stepTextArea.setCaretColor(ThemeService.getInstance().getForegroundColor());
        stepTextArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(stepTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()));
        scrollPane.setPreferredSize(new Dimension(0, 350));
        contentSection.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentSection, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTimerPanel() {
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        timerPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        timerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Timer display
        JPanel timerDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        timerDisplayPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());


        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timerLabel.setForeground(ThemeService.getInstance().getAccentColor());
        timerDisplayPanel.add(timerLabel);

        timerPanel.add(timerDisplayPanel);

        // Timer buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());

        startButton = new JButton("Start");
        styleTimerButton(startButton, true);
        startButton.addActionListener(e -> startSession());
        buttonPanel.add(startButton);

        pauseResumeButton = new JButton("Pause");
        styleTimerButton(pauseResumeButton, false);
        pauseResumeButton.setEnabled(false);
        buttonPanel.add(pauseResumeButton);

        resetTimerButton = new JButton("Reset");
        styleTimerButton(resetTimerButton, false);
        buttonPanel.add(resetTimerButton);

        timerPanel.add(buttonPanel);

        return timerPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(ThemeService.getInstance().getBackgroundColor());

        previousButton = new JButton("Previous");
        resetButton = new JButton("Reset");
        nextButton = new JButton("Next");

        styleNavigationButton(previousButton, false);
        styleNavigationButton(resetButton, false);
        styleNavigationButton(nextButton, true);

        panel.add(previousButton);
        panel.add(resetButton);
        panel.add(nextButton);

        return panel;
    }

    private void styleNavigationButton(JButton button, boolean isPrimary) {
        ThemeService theme = ThemeService.getInstance();
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        if (isPrimary) {
            button.setBackground(theme.getAccentColor());
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(theme.getBorderColor());
            button.setForeground(theme.getForegroundColor());
        }

        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

        // Store original colors
        Color originalBg = isPrimary ? theme.getAccentColor() : theme.getBorderColor();
        Color hoverBg = isPrimary ? theme.getAccentColor().darker() : theme.getBorderColor().darker();

        // Remove all existing mouse listeners to avoid duplicates
        for (java.awt.event.MouseListener ml : button.getMouseListeners()) {
            if (ml instanceof java.awt.event.MouseAdapter) {
                button.removeMouseListener(ml);
            }
        }

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverBg);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalBg);
                }
            }
        });
    }

    private void styleTimerButton(JButton button, boolean isPrimary) {
        ThemeService theme = ThemeService.getInstance();
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setBackground(theme.getAccentColor());
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(theme.getBorderColor());
            button.setForeground(theme.getForegroundColor());
        }

        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void setupEventHandlers() {
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

        // Update button states
        if (timer.isRunning()) {
            pauseResumeButton.setText("Pause");
            pauseResumeButton.setEnabled(true);
            startButton.setEnabled(false);
        } else if (timer.isPaused()) {
            pauseResumeButton.setText("Resume");
            pauseResumeButton.setEnabled(true);
            startButton.setEnabled(false);
        } else {
            pauseResumeButton.setText("Pause");
            pauseResumeButton.setEnabled(false);
            startButton.setEnabled(true);
        }
    }

    private void togglePauseResume() {
        PrayerTimer timer = rosaryService.getCurrentTimer();

        if (timer.isRunning()) {
            rosaryService.pausePrayerSession();
        } else if (timer.isPaused()) {
            rosaryService.resumePrayerSession();
        }

        updateTimerDisplay();
    }

    private void startSession() {
        if (!sessionActive) {
            rosaryService.startPrayerSession();
            sessionActive = true;
            updateTimerDisplay();
        }
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
            sessionActive = false;
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
                    "Rosary completed! \n\nTotal prayer time: " + formattedDuration,
                    "Session Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    private void loadTodaysMystery() {
        currentMystery = rosaryService.getTodaysMystery();
        loadMystery(currentMystery);
    }

    private void loadMystery(RosaryMystery mystery) {
        if (mystery != null) {
            currentMystery = mystery;
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

            // Update bead image based on step
            updateBeadImage(step);

            previousButton.setEnabled(currentStepIndex > 0);

            // On last step, change button to Finish
            if (currentStepIndex == currentSteps.size() - 1) {
                nextButton.setEnabled(true);
                if (!nextButton.getText().equals("Finish")) {
                    nextButton.setText("Finish");
                    styleNavigationButton(nextButton, true);
                }
            } else {
                nextButton.setEnabled(true);
                if (!nextButton.getText().equals("Next →")) {
                    nextButton.setText("Next →");
                    styleNavigationButton(nextButton, true);
                }
            }
        }
    }



    private void updateBeadImage(RosaryStep step) {
        String stepId = step.getId();
        String imagePath = getBeadImagePath(stepId);

        beadImageLabel.removeAll();
        beadImageLabel.setLayout(new BorderLayout());

        try {
            // Try to load the image from resources
            java.net.URL imageUrl = getClass().getResource(imagePath);

            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                // Scale image to fit the label while maintaining aspect ratio
                Image scaledImage = originalIcon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                JLabel imageLabel = new JLabel(scaledIcon);
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                beadImageLabel.add(imageLabel, BorderLayout.CENTER);
            } else {
                // Show placeholder if image not found
                showImagePlaceholder(stepId, imagePath);
            }
        } catch (Exception e) {
            // Show placeholder on error
            showImagePlaceholder(stepId, imagePath);
        }

        beadImageLabel.revalidate();
        beadImageLabel.repaint();
    }

    private void showImagePlaceholder(String stepId, String imagePath) {
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setBackground(ThemeService.getInstance().getBackgroundColor());

        JLabel placeholderText = new JLabel("<html><center><b>" + getBeadType(stepId) + "</b><br/><br/>Image Placeholder<br/><small>" + imagePath + "</small></center></html>");
        placeholderText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        placeholderText.setForeground(ThemeService.getInstance().getForegroundColor());
        placeholderText.setHorizontalAlignment(SwingConstants.CENTER);
        placeholder.add(placeholderText, BorderLayout.CENTER);

        beadImageLabel.add(placeholder, BorderLayout.CENTER);
    }

    private String getBeadImagePath(String stepId) {
        // Map step IDs to image file paths
        // Customize these paths based on your image naming convention
        if (stepId.contains("sign_cross")) {
            return "/images/beads/crucifix.png";
        } else if (stepId.contains("our_father")) {
            return "/images/beads/our_father_bead.png";
        } else if (stepId.contains("hail_mary")) {
            return "/images/beads/hail_mary_bead.png";
        } else if (stepId.contains("glory_be")) {
            return "/images/beads/glory_be_bead.png";
        } else if (stepId.contains("mystery")) {
            return "/images/beads/mystery_bead.png";
        } else {
            return "/images/beads/default_bead.png";
        }
    }

    private String getBeadType(String stepId) {
        // Return user-friendly bead type names
        if (stepId.contains("sign_cross")) {
            return "Crucifix";
        } else if (stepId.contains("our_father")) {
            return "Our Father Bead";
        } else if (stepId.contains("hail_mary")) {
            return "Hail Mary Bead";
        } else if (stepId.contains("glory_be")) {
            return "Glory Be";
        } else if (stepId.contains("mystery")) {
            return "Mystery Meditation";
        } else {
            return "Prayer Bead";
        }
    }

    public void refreshContent() {
        // Apply current theme
        ThemeService theme = ThemeService.getInstance();
        setBackground(theme.getBackgroundColor());
        theme.applyTheme(this);

        // Reset timer when language changes
        rosaryService.resetPrayerTimer();
        sessionActive = false;
        updateTimerDisplay();

        // Reload current mystery
        if (currentMystery != null) {
            loadMystery(currentMystery);
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