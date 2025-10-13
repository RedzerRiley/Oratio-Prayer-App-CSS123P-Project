package com.oratio.services;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing background music playback
 * FIX: The class now uses getResourceAsStream() for reliable file loading.
 * FIX: Mute/unmute logic corrected to prevent music from stopping.
 */
public class MusicService {
    private static MusicService instance;

    private Clip currentClip;
    private FloatControl volumeControl;
    private boolean isMuted = false;
    private float currentVolume = 0.5f; // 50% volume by default
    private boolean isPlaying = false;
    private String currentTrack;

    private List<MusicChangeListener> listeners = new ArrayList<>();

    // Paths assumed to be relative to the classpath root (e.g., inside the 'resources' folder).
    private final String[] MUSIC_TRACKS = {
            "music/gregorian_chant.wav",
            "music/ave_maria.wav",
            "music/peaceful_hymn.wav",
            "music/meditation.wav"
    };

    private int currentTrackIndex = 0;

    private MusicService() {
    }

    public static MusicService getInstance() {
        if (instance == null) {
            instance = new MusicService();
        }
        return instance;
    }

    /**
     * Plays background music from a resource file path (e.g., "music/track.wav")
     */
    public void playMusic(String filePath) {
        try {
            stopMusic(); // Stop any currently playing music

            // Use MusicService.class.getResourceAsStream() with a leading slash
            // to correctly resolve the path from the classpath root.
            InputStream inputStream = MusicService.class.getResourceAsStream("/" + filePath);

            if (inputStream == null) {
                System.err.println("Audio resource not found in classpath: /" + filePath);

                // Optional: Attempt File access as a last resort
                File audioFile = new File(filePath);
                if (audioFile.exists()) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    startClip(audioStream, filePath);
                    return;
                } else {
                    System.err.println("Audio file not found: " + filePath);
                    return;
                }
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
            startClip(audioStream, filePath);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to start the clip once the AudioInputStream is ready.
     */
    private void startClip(AudioInputStream audioStream, String filePath) throws LineUnavailableException, IOException {
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);

        if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(currentVolume);
        }

        currentClip.loop(Clip.LOOP_CONTINUOUSLY);
        currentClip.start();

        isPlaying = true;
        currentTrack = filePath;
        notifyListeners();

        System.out.println("Playing music: " + filePath);
    }

    /**
     * Plays the default background music (first track)
     */
    public void playDefaultMusic() {
        playMusic(MUSIC_TRACKS[0]);
    }

    /**
     * Plays music by track index
     */
    public void playTrack(int index) {
        if (index >= 0 && index < MUSIC_TRACKS.length) {
            currentTrackIndex = index;
            playMusic(MUSIC_TRACKS[index]);
        }
    }

    /**
     * Selects and plays a track by its display name
     */
    public void selectTrack(String trackName) {
        for (int i = 0; i < MUSIC_TRACKS.length; i++) {
            String displayName = getTrackDisplayName(MUSIC_TRACKS[i]);
            if (displayName.equals(trackName)) {
                playTrack(i);
                return;
            }
        }
    }

    /**
     * Gets display name from file path - helper method
     */
    private String getTrackDisplayName(String filePath) {
        String name = new File(filePath).getName();
        return name.replace(".wav", "").replace("_", " ");
    }

    /**
     * Plays the next track in the list
     */
    public void playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % MUSIC_TRACKS.length;
        playTrack(currentTrackIndex);
    }

    /**
     * Plays the previous track in the list
     */
    public void playPreviousTrack() {
        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = MUSIC_TRACKS.length - 1;
        }
        playTrack(currentTrackIndex);
    }

    /**
     * Stops the currently playing music
     */
    public void stopMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
            isPlaying = false;
            notifyListeners();
        }
    }

    /**
     * Pauses the music
     */
    public void pauseMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            isPlaying = false;
            notifyListeners();
        }
    }

    /**
     * Resumes the music
     */
    public void resumeMusic() {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.start();
            isPlaying = true;
            notifyListeners();
        }
    }

    /**
     * Toggles play/pause
     */
    public void togglePlayPause() {
        if (isPlaying) {
            pauseMusic();
        } else {
            if (currentClip == null) {
                playDefaultMusic();
            } else {
                resumeMusic();
            }
        }
    }

    /**
     * Sets the volume (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        currentVolume = Math.max(0.0f, Math.min(1.0f, volume));

        // Only apply gain if music is not muted
        if (volumeControl != null && !isMuted) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float gain = min + (max - min) * currentVolume;
            volumeControl.setValue(gain);
        }
        notifyListeners();
    }

    /**
     * Gets the current volume (0.0 to 1.0)
     */
    public float getVolume() {
        return currentVolume;
    }

    /**
     * Mutes the music
     */
    public void mute() {
        if (volumeControl != null) {
            // Set volume to minimum while setting isMuted flag
            volumeControl.setValue(volumeControl.getMinimum());
            isMuted = true;
            notifyListeners();
        }
    }

    /**
     * Unmutes the music
     */
    public void unmute() {
        if (volumeControl != null) {
            // *** FIX: Set isMuted flag FIRST, then call setVolume to restore gain. ***
            isMuted = false;
            setVolume(currentVolume);
            // notifyListeners is called inside setVolume
        }
    }

    /**
     * Toggles mute
     */
    public void toggleMute() {
        if (isMuted) {
            unmute();
        } else {
            mute();
        }
    }

    /**
     * Checks if music is currently playing
     */
    public boolean isPlaying() {
        return isPlaying && currentClip != null && currentClip.isRunning();
    }

    /**
     * Checks if music is muted
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Gets the current track name
     */
    public String getCurrentTrackName() {
        if (currentTrack == null) return "No track";
        return getTrackDisplayName(currentTrack);
    }

    /**
     * Gets all available tracks
     */
    public String[] getAvailableTracks() {
        String[] trackNames = new String[MUSIC_TRACKS.length];
        for (int i = 0; i < MUSIC_TRACKS.length; i++) {
            trackNames[i] = getTrackDisplayName(MUSIC_TRACKS[i]);
        }
        return trackNames;
    }

    /**
     * Gets the current track index
     */
    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    /**
     * Adds a music change listener
     */
    public void addMusicChangeListener(MusicChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a music change listener
     */
    public void removeMusicChangeListener(MusicChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners of music state changes
     */
    private void notifyListeners() {
        for (MusicChangeListener listener : listeners) {
            listener.onMusicStateChanged(isPlaying, isMuted, currentVolume);
        }
    }

    /**
     * Cleans up resources
     */
    public void dispose() {
        stopMusic();
        if (currentClip != null) {
            currentClip.close();
        }
    }

    /**
     * Interface for listening to music state changes
     */
    public interface MusicChangeListener {
        void onMusicStateChanged(boolean isPlaying, boolean isMuted, float volume);
    }
}