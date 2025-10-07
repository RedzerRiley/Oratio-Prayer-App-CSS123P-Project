// File: src/main/java/com/oratio/gui/MainFrame.java
package com.oratio.gui;

import com.oratio.services.LanguageService;
import com.oratio.services.ThemeService;
import com.oratio.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Main application window with tabbed interface
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JComboBox<String> languageSelector;

    private PrayersPanel prayersPanel;
    private RosaryPanel rosaryPanel;
    private PsalmsPanel psalmsPanel;
    private NovenaPanel novenaPanel;
    //    private FavoritesPanel favoritesPanel;
    private NotesPanel notesPanel;

    private LanguageService languageService;
    private ThemeService themeService;

    public MainFrame() {
        initializeServices();
        initializeGUI();
//        setupEventHandlers();
    }

    private void initializeServices() {
        languageService = LanguageService.getInstance();
        themeService = ThemeService.getInstance();
    }

    private void initializeGUI() {
        setTitle("Oratio - A Catholic Prayer App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Create main layout with modern styling
        setLayout(new BorderLayout(0, 0));

        // Create top header with logo
        JPanel headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Create modern tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        styleModernTabs();

        // Initialize panels
        prayersPanel = new PrayersPanel();
        rosaryPanel = new RosaryPanel();
        psalmsPanel = new PsalmsPanel();
        novenaPanel = new NovenaPanel();
//        favoritesPanel = new FavoritesPanel();
        notesPanel = new NotesPanel();

        // Add tabs with icons and modern styling
        addModernTab("Daily Prayers", prayersPanel, "Daily prayers and devotions");
        addModernTab("Rosary", rosaryPanel, "Rosary guide with mysteries");
        addModernTab("Psalms", psalmsPanel, "Browse and search psalms");
        addModernTab("Novenas", novenaPanel, "Common novenas and devotions");
//        addModernTab("⭐ Favorites", favoritesPanel, "Your favorite prayers");
        addModernTab("Notes", notesPanel, "Prayer journal and reflections");

        // Wrap tabbed pane in a panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Apply modern theme
        themeService.applyTheme(this);
        applyModernStyling();
    }

    /**
     * Creates the modern header panel with logo and language selector
     */
    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(new Color(74, 85, 162)); // Catholic blue/purple color
        headerPanel.setPreferredSize(new Dimension(getWidth(), 90)); // adjust height

        // Left side - Logo and app name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // Try to load logo image
        JPanel logoPanel = createLogoPanel();
        leftPanel.add(logoPanel);


        // App name next to logo
        JPanel brandingPanel = new JPanel();
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setOpaque(false);

//        JLabel appLabel = new JLabel("Oratio");
//        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        appLabel.setForeground(Color.WHITE);
//
//        JLabel subtitleLabel = new JLabel("A Catholic Prayer App");
//        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

//        brandingPanel.add(appLabel);
//        brandingPanel.add(Box.createVerticalStrut(2));
//        brandingPanel.add(subtitleLabel);

        leftPanel.add(brandingPanel);

        // Right side - Language selector only
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Language selector with modern styling
        JLabel langLabel = new JLabel("Language:");
        langLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        langLabel.setForeground(Color.WHITE);

        languageSelector = new JComboBox<>(Constants.SUPPORTED_LANGUAGES);
        languageSelector.setSelectedItem(languageService.getCurrentLanguage());
        styleModernComboBox(languageSelector);

        rightPanel.add(langLabel);
        rightPanel.add(languageSelector);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * A custom JPanel that renders the logo crisply and proportionally.
     */
    private JPanel createLogoPanel() {
        return new JPanel() {
            private Image logoImage;

            {
                // Load logo once
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
                        System.out.println("Logo loaded from: " + path);
                        break;
                    }
                }

                // Slightly smaller than before for balance
                setPreferredSize(new Dimension(150, 80));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (logoImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Resize proportionally for a 500x500 image
                    int targetHeight = 150; // visible height in header
                    int targetWidth = 150;  // same width since it’s square

                    int x = (getWidth() - targetWidth) / 2;
                    int y = (getHeight() - targetHeight) / 2;

                    g2d.drawImage(logoImage, x, y, targetWidth, targetHeight, null);
                    g2d.dispose();
                } else {
                    g.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    g.setColor(Color.BLACK);
                    g.drawString(" Oratio", 10, getHeight() / 2 + 10);
                }
            }
        };
    }




    private void styleModernTabs() {
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Custom tab styling
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 45; // Taller tabs for modern look
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                // Remove default tab borders for cleaner look
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                               Rectangle[] rects, int tabIndex, Rectangle iconRect,
                                               Rectangle textRect, boolean isSelected) {
                // Remove focus indicators for cleaner look
            }
        });
    }

    private void addModernTab(String title, JPanel panel, String tooltip) {
        tabbedPane.addTab(title, null, panel, tooltip);
        int index = tabbedPane.getTabCount() - 1;

        // Style individual tabs
        tabbedPane.setBackgroundAt(index, new Color(248, 249, 250));
        tabbedPane.setForegroundAt(index, new Color(33, 37, 41));
    }

    private void styleModernComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setFocusable(false);
    }

    private void applyModernStyling() {
        // Apply modern colors and fonts throughout
        setBackground(new Color(255, 255, 255));

        // Modern card-like appearance for the main container
        getRootPane().setBorder(BorderFactory.createEmptyBorder());
    }

}