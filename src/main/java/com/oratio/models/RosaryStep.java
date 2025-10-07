
// File: src/main/java/com/oratio/models/RosaryStep.java
package com.oratio.models;

/**
 * Represents a step in the rosary prayer sequence
 */
public class RosaryStep {
    private String id;
    private String stepName;
    private String prayerType; // e.g., "Our Father", "Hail Mary", "Glory Be"
    private int repetitions;
    private String meditation; // For mystery meditations

    public RosaryStep(String id, String stepName, String prayerType, int repetitions) {
        this.id = id;
        this.stepName = stepName;
        this.prayerType = prayerType;
        this.repetitions = repetitions;
    }

    public RosaryStep(String id, String stepName, String prayerType, int repetitions, String meditation) {
        this(id, stepName, prayerType, repetitions);
        this.meditation = meditation;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }

    public String getPrayerType() { return prayerType; }
    public void setPrayerType(String prayerType) { this.prayerType = prayerType; }

    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public String getMeditation() { return meditation; }
    public void setMeditation(String meditation) { this.meditation = meditation; }
}
