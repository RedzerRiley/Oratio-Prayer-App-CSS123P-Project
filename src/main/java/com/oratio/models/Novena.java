
// File: src/main/java/com/oratio/models/Novena.java
package com.oratio.models;

/**
 * Represents a novena
 */
public class Novena {
    private String id;
    private String name;
    private String description;
    private int durationDays;
    private boolean isFavorite;

    public Novena(String id, String name, String description, int durationDays) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationDays = durationDays;
        this.isFavorite = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    @Override
    public String toString() {
        return name;
    }
}
