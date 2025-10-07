
// File: src/main/java/com/oratio/models/RosaryMystery.java
package com.oratio.models;

/**
 * Represents a rosary mystery (Joyful, Sorrowful, Glorious, Luminous)
 */
public class RosaryMystery {
    private String id;
    private String name;
    private MysteryType type;
    private String[] dayOfWeek; // Days when this mystery is typically prayed

    public enum MysteryType {
        JOYFUL, SORROWFUL, GLORIOUS, LUMINOUS
    }

    public RosaryMystery(String id, String name, MysteryType type, String[] dayOfWeek) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MysteryType getType() { return type; }
    public void setType(MysteryType type) { this.type = type; }

    public String[] getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String[] dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    @Override
    public String toString() {
        return name;
    }
}