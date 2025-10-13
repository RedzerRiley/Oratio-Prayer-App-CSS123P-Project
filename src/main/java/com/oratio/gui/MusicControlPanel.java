package com.oratio.gui;

import com.oratio.services.MusicService;
import com.oratio.services.ThemeService;

import javax.swing.*;
import java.awt.*;

/**
 * Compact music control panel for the header (Text-only version)
 */
public class MusicControlPanel extends JPanel implements MusicService.MusicChangeListener {
    private JButton playPauseButton;
    private JButton muteButton;
    private JSlider volumeSlider;
    private JComboBox<String> trackSelector;

    private MusicService musicService;
    private ThemeService themeService;

    private boolean isUpdating = false; // Prevent circular updates

    public MusicControlPanel() {
        musicService = MusicService.getInstance();
        themeService = ThemeService.getInstance();

        initializeGUI();
        setupEventHandlers();

        // Register as listener
        musicService.addMusicChangeListener(this);

        // Update UI after everything is initialized
        SwingUtilities.invokeLater(this::updateUI);
    }

    private void initializeGUI() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        setOpaque(false);

        // Track selector combo box
        trackSelector = new JComboBox<>();
        trackSelector.setPreferredSize(new Dimension(180, 32));
        trackSelector.setToolTipText("Select Music Track");
        styleTrackComboBox(trackSelector);
        loadTracksIntoComboBox();

        // Play/Pause button (text-based)
        playPauseButton = createMusicButton("Play");
        playPauseButton.setToolTipText("Play Music");

        // Mute button (text-based)
        muteButton = createMusicButton("Mute");
        muteButton.setToolTipText("Mute");

        // Volume slider
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setPreferredSize(new Dimension(100, 30));
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setToolTipText("Volume");

        add(trackSelector);
        add(playPauseButton);
        add(muteButton);
        add(volumeSlider);
    }

    private void loadTracksIntoComboBox() {
        trackSelector.removeAllItems();
        String[] tracks = musicService.getAvailableTracks();

        if (tracks != null && tracks.length > 0) {
            for (String track : tracks) {
                trackSelector.addItem(track);
            }
            String currentTrack = musicService.getCurrentTrackName();
            if (currentTrack != null && !currentTrack.equals("No track")) {
                trackSelector.setSelectedItem(currentTrack);
            }
        } else {
            trackSelector.addItem("No tracks available");
        }
    }

    private void styleTrackComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(new Color(74, 85, 162)); // Blue theme
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setFocusable(true);
        comboBox.setOpaque(true);
    }

    private JButton createMusicButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(70, 32));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    private void setupEventHandlers() {
        trackSelector.addActionListener(e -> {
            if (!isUpdating && trackSelector.getSelectedItem() != null) {
                String selectedTrack = (String) trackSelector.getSelectedItem();
                if (!selectedTrack.equals("No tracks available")) {
                    musicService.selectTrack(selectedTrack);
                }
            }
        });

        playPauseButton.addActionListener(e -> {
            musicService.togglePlayPause();
        });

        muteButton.addActionListener(e -> {
            musicService.toggleMute();
        });

        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting()) {
                float volume = volumeSlider.getValue() / 100.0f;
                musicService.setVolume(volume);
            }
        });
    }

    @Override
    public void onMusicStateChanged(boolean isPlaying, boolean isMuted, float volume) {
        SwingUtilities.invokeLater(this::updateUI);
    }

    @Override
    public void updateUI() {
        if (musicService == null) return;

        isUpdating = true;
        try {
            // Toggle play/pause text
            if (musicService.isPlaying()) {
                playPauseButton.setText("Pause");
                playPauseButton.setToolTipText("Pause Music");
            } else {
                playPauseButton.setText("Play");
                playPauseButton.setToolTipText("Play Music");
            }

            // Toggle mute/unmute text
            if (musicService.isMuted()) {
                muteButton.setText("Unmute");
                muteButton.setToolTipText("Unmute Sound");
            } else {
                muteButton.setText("Mute");
                muteButton.setToolTipText("Mute Sound");
            }

            // Update volume slider
            volumeSlider.setValue((int) (musicService.getVolume() * 100));

            // Update track selection
            String currentTrack = musicService.getCurrentTrackName();
            if (currentTrack != null && !currentTrack.equals("No track")) {
                trackSelector.setSelectedItem(currentTrack);
            }
        } finally {
            isUpdating = false;
        }
    }

    public void dispose() {
        if (musicService != null) {
            musicService.removeMusicChangeListener(this);
        }
    }
}
