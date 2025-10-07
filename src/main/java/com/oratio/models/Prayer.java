// File: src/main/java/com/oratio/models/Prayer.java
package com.oratio.models;

/**
 * Represents a prayer in the application
 */
public class Prayer {
    private String id;
    private String name;
    private String category;
    private boolean isCompleted;
    private boolean isFavorite;

    public Prayer(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isCompleted = false;
        this.isFavorite = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    @Override
    public String toString() {
        return name;
    }
}


