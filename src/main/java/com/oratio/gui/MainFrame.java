package com.oratio.gui;

import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;
import com.oratio.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Main application window with modern sidebar navigation
 */
public class MainFrame extends JFrame {
    private static final Color CATHOLIC_BLUE = new Color(74, 85, 162);
    private static final Color SIDEBAR_DARK = new Color(45, 52, 64);
    private static final Color SIDEBAR_LIGHT = new Color(248, 249, 250);

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JComboBox<String> languageSelector;
    private JToggleButton darkModeToggle;
    private MusicControlPanel musicControlPanel;

    private PrayersPanel prayersPanel;
    private RosaryPanel rosaryPanel;
    private PsalmsPanel psalmsPanel;
    private NovenaPanel novenaPanel;
    private NotesPanel notesPanel;

    private LanguageService languageService;
    private ThemeService themeService;

    private JButton selectedNavButton;

    public MainFrame() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
    }

    private void initializeServices() {
        languageService = LanguageService.getInstance();
        themeService = ThemeService.getInstance();
    }

    private void initializeGUI() {
        setTitle("Oratio - A Catholic Prayer App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));

        setLayout(new BorderLayout(0, 0));

        // Create header
        headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Create main container with sidebar and content
        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(themeService.getBackgroundColor());

        // Create sidebar
        sidebarPanel = createModernSidebar();
        mainContainer.add(sidebarPanel, BorderLayout.WEST);

        // Create content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(themeService.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize panels
        prayersPanel = new PrayersPanel();
        rosaryPanel = new RosaryPanel();
        psalmsPanel = new PsalmsPanel();
        novenaPanel = new NovenaPanel();
        notesPanel = new NotesPanel();

        // Show default panel
        showPanel(prayersPanel);

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);

        // Apply theme
        themeService.applyTheme(this);
        applyModernStyling();

        // Ensure header stays Catholic Blue
        headerPanel.setBackground(CATHOLIC_BLUE);

        SwingUtilities.invokeLater(this::applyThemeToAllComponents);
    }

    // In your MainFrame.java, update the createModernHeader() method:

    private JPanel createModernHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        header.setBackground(CATHOLIC_BLUE);
        header.setPreferredSize(new Dimension(getWidth(), 100));

        // Left side - Logo and app name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JPanel logoPanel = createLogoPanel();
        leftPanel.add(logoPanel);

        // Center - Music controls
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);

        try {
            musicControlPanel = new MusicControlPanel();
            centerPanel.add(musicControlPanel);
        } catch (Exception e) {
            // If music control fails to load, show error message
            JLabel errorLabel = new JLabel("Music controls unavailable");
            errorLabel.setForeground(Color.WHITE);
            errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            centerPanel.add(errorLabel);
            System.err.println("Failed to initialize music controls: " + e.getMessage());
        }

        // Right side - Controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Dark mode toggle with icon
        darkModeToggle = new JToggleButton();
        darkModeToggle.setSelected(themeService.isDarkMode());
        updateToggleButtonAppearance();
        styleToggleButton(darkModeToggle);

        // Language selector
        JLabel langLabel = new JLabel("Language:");
        langLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        langLabel.setForeground(Color.WHITE);

        languageSelector = new JComboBox<>(Constants.SUPPORTED_LANGUAGES);
        languageSelector.setSelectedItem(languageService.getCurrentLanguage());
        styleModernComboBox(languageSelector);

        rightPanel.add(darkModeToggle);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(langLabel);
        rightPanel.add(languageSelector);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(centerPanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    /**
     * Creates the modern sidebar navigation
     */
    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        updateSidebarColors(sidebar);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
                themeService.getBorderColor()));

        // Add padding at top
        sidebar.add(Box.createVerticalStrut(30));

        // Navigation title
        JLabel navTitle = new JLabel("  NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(themeService.getSecondaryTextColor());
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 0));
        sidebar.add(navTitle);

        // Navigation buttons
        JButton prayersBtn = createNavButton("", "Daily Prayers", true);
        JButton rosaryBtn = createNavButton("", "Rosary", false);
        JButton psalmsBtn = createNavButton("", "Psalms", false);
        JButton novenasBtn = createNavButton("", "Novenas", false);
        JButton notesBtn = createNavButton("", "Notes", false);

        sidebar.add(prayersBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(rosaryBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(psalmsBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(novenasBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(notesBtn);

        // Add glue to push everything to top
        sidebar.add(Box.createVerticalGlue());

        // Add bottom info
        JPanel bottomInfo = new JPanel(new BorderLayout());
        bottomInfo.setOpaque(false);
        bottomInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomInfo.setMaximumSize(new Dimension(250, 100));

        JLabel versionLabel = new JLabel("Oratio v1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(themeService.getSecondaryTextColor());
        bottomInfo.add(versionLabel, BorderLayout.SOUTH);

        sidebar.add(bottomInfo);

        // Set up click handlers
        prayersBtn.addActionListener(e -> {
            selectNavButton(prayersBtn);
            showPanel(prayersPanel);
        });
        rosaryBtn.addActionListener(e -> {
            selectNavButton(rosaryBtn);
            showPanel(rosaryPanel);
        });
        psalmsBtn.addActionListener(e -> {
            selectNavButton(psalmsBtn);
            showPanel(psalmsPanel);
        });
        novenasBtn.addActionListener(e -> {
            selectNavButton(novenasBtn);
            showPanel(novenaPanel);
        });
        notesBtn.addActionListener(e -> {
            selectNavButton(notesBtn);
            showPanel(notesPanel);
        });

        // Select first button by default
        selectNavButton(prayersBtn);

        return sidebar;
    }

    /**
     * Creates a navigation button
     */
    private JButton createNavButton(String icon, String text, boolean isSelected) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(15, 0));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setMaximumSize(new Dimension(250, 55));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        // Text label
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(iconLabel);
        contentPanel.add(textLabel);

        button.add(contentPanel, BorderLayout.WEST);

        // Style based on theme
        updateNavButtonStyle(button, iconLabel, textLabel, isSelected);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedNavButton) {
                    button.setBackground(themeService.isDarkMode() ?
                            new Color(55, 62, 74) : new Color(240, 242, 245));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedNavButton) {
                    button.setBackground(getSidebarColor());
                }
            }
        });

        return button;
    }

    /**
     * Updates navigation button style
     */
    private void updateNavButtonStyle(JButton button, JLabel iconLabel,
                                      JLabel textLabel, boolean isSelected) {
        if (isSelected) {
            button.setBackground(themeService.getAccentColor());
            button.setOpaque(true);
            button.setBorderPainted(false);
            iconLabel.setForeground(Color.WHITE);
            textLabel.setForeground(Color.WHITE);
        } else {
            button.setBackground(getSidebarColor());
            button.setOpaque(true);
            button.setBorderPainted(false);
            iconLabel.setForeground(themeService.getForegroundColor());
            textLabel.setForeground(themeService.getForegroundColor());
        }
    }

    /**
     * Selects a navigation button
     */
    private void selectNavButton(JButton button) {
        // Deselect previous button
        if (selectedNavButton != null) {
            Component[] components = ((JPanel) selectedNavButton.getComponent(0)).getComponents();
            JLabel iconLabel = (JLabel) components[0];
            JLabel textLabel = (JLabel) components[1];
            updateNavButtonStyle(selectedNavButton, iconLabel, textLabel, false);
        }

        // Select new button
        selectedNavButton = button;
        Component[] components = ((JPanel) button.getComponent(0)).getComponents();
        JLabel iconLabel = (JLabel) components[0];
        JLabel textLabel = (JLabel) components[1];
        updateNavButtonStyle(button, iconLabel, textLabel, true);
    }

    /**
     * Shows a panel in the content area
     */
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Gets sidebar background color
     */
    private Color getSidebarColor() {
        return themeService.isDarkMode() ? SIDEBAR_DARK : SIDEBAR_LIGHT;
    }

    /**
     * Updates sidebar colors
     */
    private void updateSidebarColors(JPanel sidebar) {
        sidebar.setBackground(getSidebarColor());
    }

    /**
     * Style the dark mode toggle button with better contrast
     */
    private void styleToggleButton(JToggleButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Update colors based on toggle state
        updateToggleButtonColors();

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setPreferredSize(new Dimension(110, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isSelected()) {
                    button.setBackground(new Color(35, 40, 50));
                } else {
                    button.setBackground(new Color(245, 245, 245));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                updateToggleButtonColors();
            }
        });
    }

    /**
     * Updates the toggle button colors based on current mode
     */
    private void updateToggleButtonColors() {
        if (darkModeToggle.isSelected()) {
            // Dark mode is ON - show dark button
            darkModeToggle.setBackground(new Color(45, 52, 64));
            darkModeToggle.setForeground(Color.WHITE);
        } else {
            // Light mode is ON - show light button
            darkModeToggle.setBackground(Color.WHITE);
            darkModeToggle.setForeground(CATHOLIC_BLUE);
        }
    }

    /**
     * Updates the toggle button text based on current mode
     */
    private void updateToggleButtonAppearance() {
        if (themeService.isDarkMode()) {
            darkModeToggle.setText("Dark Mode");
            darkModeToggle.setToolTipText("Currently in Dark Mode - Click to switch to Light Mode");
        } else {
            darkModeToggle.setText("Light Mode");
            darkModeToggle.setToolTipText("Currently in Light Mode - Click to switch to Dark Mode");
        }
        updateToggleButtonColors();
    }

    /**
     * Creates logo panel
     */
    private JPanel createLogoPanel() {
        return new JPanel() {
            private Image logoImage;

            {
                String[] logoPaths = {
                        "src/main/resources/images/logo.png",
                        "resources/images/logo.png",
                        "images/logo.png",
                        "logo.png"
                };

                for (String path : logoPaths) {
                    File logoFile = new File(path);
                    if (logoFile.exists()) {
                        logoImage = new ImageIcon(path).getImage();
                        break;
                    }
                }

                setPreferredSize(new Dimension(150, 60));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (logoImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    int targetHeight = 120;
                    int targetWidth = 120;
                    int x = (getWidth() - targetWidth) / 2;
                    int y = (getHeight() - targetHeight) / 2;

                    g2d.drawImage(logoImage, x, y, targetWidth, targetHeight, null);
                    g2d.dispose();
                } else {
                    g.setFont(new Font("Segoe UI", Font.BOLD, 28));
                    g.setColor(Color.WHITE);
                    g.drawString("Oratio", 10, getHeight() / 2 + 10);
                }
            }
        };
    }

    private void styleModernComboBox(JComboBox comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(CATHOLIC_BLUE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setFocusable(false);
        comboBox.setOpaque(true);

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                if (!isSelected) {
                    c.setBackground(Color.WHITE);
                    c.setForeground(CATHOLIC_BLUE);
                } else {
                    c.setBackground(CATHOLIC_BLUE);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
    }

    private void applyModernStyling() {
        setBackground(themeService.getBackgroundColor());
        getRootPane().setBorder(BorderFactory.createEmptyBorder());

        // Force header to stay Catholic Blue
        if (headerPanel != null) {
            headerPanel.setBackground(CATHOLIC_BLUE);
            headerPanel.setOpaque(true);
        }
    }

    private void setupEventHandlers() {
        darkModeToggle.addActionListener(e -> {
            themeService.toggleTheme();
            updateToggleButtonAppearance();
            applyThemeToAllComponents();
            repaint();
        });

        languageSelector.addActionListener(e -> {
            String selectedLanguage = (String) languageSelector.getSelectedItem();
            languageService.setCurrentLanguage(selectedLanguage);
            refreshAllPanels();
        });
    }

    /**
     * Apply theme changes to all components
     */
    private void applyThemeToAllComponents() {
        setBackground(themeService.getBackgroundColor());
        getContentPane().setBackground(themeService.getBackgroundColor());
        contentPanel.setBackground(themeService.getBackgroundColor());

        // CRITICAL: Keep header Catholic Blue always - prevent theme from overriding
        headerPanel.setBackground(CATHOLIC_BLUE);
        headerPanel.setOpaque(true);

        // Keep all header child components with proper colors
        for (Component comp : headerPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setOpaque(false); // Make child panels transparent to show blue background
            }
        }

        // Update sidebar
        updateSidebarColors(sidebarPanel);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
                themeService.getBorderColor()));

        // Update all navigation buttons
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                Component[] components = ((JPanel) button.getComponent(0)).getComponents();
                if (components.length >= 2) {
                    JLabel iconLabel = (JLabel) components[0];
                    JLabel textLabel = (JLabel) components[1];
                    updateNavButtonStyle(button, iconLabel, textLabel,
                            button == selectedNavButton);
                }
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().equals("  NAVIGATION")) {
                    label.setForeground(themeService.getSecondaryTextColor());
                }
            } else if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component child : panel.getComponents()) {
                    if (child instanceof JLabel) {
                        ((JLabel) child).setForeground(themeService.getSecondaryTextColor());
                    }
                }
            }
        }

        styleToggleButton(darkModeToggle);
        updateToggleButtonAppearance();
        styleModernComboBox(languageSelector);

        themeService.applyTheme(prayersPanel);
        themeService.applyTheme(rosaryPanel);
        themeService.applyTheme(psalmsPanel);
        themeService.applyTheme(novenaPanel);
        themeService.applyTheme(notesPanel);

        refreshAllPanels();

        if (psalmsPanel != null) {
            try {
                java.lang.reflect.Method refreshTheme =
                        psalmsPanel.getClass().getMethod("refreshTheme");
                refreshTheme.invoke(psalmsPanel);
            } catch (Exception e) {
                // Method might not exist
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
    }

    private void refreshAllPanels() {
        prayersPanel.refreshContent();
        rosaryPanel.refreshContent();
        psalmsPanel.refreshContent();
        novenaPanel.refreshContent();
        notesPanel.refreshContent();
    }
}