
// File: src/main/java/com/oratio/models/Psalm.java
package com.oratio.models;

/**
 * Represents a psalm
 */
public class Psalm {
    private int number;
    private String title;
    private boolean isFavorite;

    public Psalm(int number, String title) {
        this.number = number;
        this.title = title;
        this.isFavorite = false;
    }

    // Getters and setters
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    @Override
    public String toString() {
        return String.format("Psalm %d: %s", number, title);
    }
}
