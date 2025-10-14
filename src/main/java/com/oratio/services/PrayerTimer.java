package com.oratio.models;

import java.time.Duration;
import java.time.Instant;

/**
 * Model class for tracking prayer duration
 */
public class PrayerTimer {
    private Instant startTime;
    private Instant pauseTime;
    private Duration accumulatedTime;
    private TimerState state;

    public enum TimerState {
        STOPPED,
        RUNNING,
        PAUSED
    }

    public PrayerTimer() {
        this.accumulatedTime = Duration.ZERO;
        this.state = TimerState.STOPPED;
    }

    /**
     * Starts or resumes the timer
     */
    public void start() {
        if (state == TimerState.STOPPED || state == TimerState.PAUSED) {
            startTime = Instant.now();
            state = TimerState.RUNNING;
        }
    }

    /**
     * Pauses the timer and accumulates elapsed time
     */
    public void pause() {
        if (state == TimerState.RUNNING) {
            pauseTime = Instant.now();
            accumulatedTime = accumulatedTime.plus(Duration.between(startTime, pauseTime));
            state = TimerState.PAUSED;
        }
    }

    /**
     * Resets the timer to zero
     */
    public void reset() {
        startTime = null;
        pauseTime = null;
        accumulatedTime = Duration.ZERO;
        state = TimerState.STOPPED;
    }

    /**
     * Gets the current elapsed time
     * @return Duration of elapsed time
     */
    public Duration getElapsedTime() {
        Duration totalTime = accumulatedTime;

        if (state == TimerState.RUNNING && startTime != null) {
            totalTime = totalTime.plus(Duration.between(startTime, Instant.now()));
        }

        return totalTime;
    }

    /**
     * Formats elapsed time as MM:SS
     * @return Formatted time string
     */
    public String getFormattedTime() {
        Duration elapsed = getElapsedTime();
        long minutes = elapsed.toMinutes();
        long seconds = elapsed.minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Formats elapsed time as HH:MM:SS for session summary
     * @return Formatted time string with hours
     */
    public String getFormattedSessionTime() {
        Duration elapsed = getElapsedTime();
        long hours = elapsed.toHours();
        long minutes = elapsed.minusHours(hours).toMinutes();
        long seconds = elapsed.minusHours(hours).minusMinutes(minutes).getSeconds();

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public TimerState getState() {
        return state;
    }

    public boolean isRunning() {
        return state == TimerState.RUNNING;
    }

    public boolean isPaused() {
        return state == TimerState.PAUSED;
    }

    public boolean isStopped() {
        return state == TimerState.STOPPED;
    }
}