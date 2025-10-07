// File: src/main/java/com/oratio/services/PsalmService.java
package com.oratio.services;

import com.oratio.models.Psalm;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing psalms with text highlighting support
 */
public class PsalmService {
    private static PsalmService instance;
    private List<Psalm> allPsalms;
    private Set<Integer> favoritePsalmNumbers = new HashSet<>();
    private LanguageService languageService;

    // Highlight management
    private Map<String, List<HighlightData>> highlightsByPsalm = new HashMap<>();
    private static final String HIGHLIGHTS_FILE = "user_data/psalm_highlights.dat";

    private PsalmService() {
        languageService = LanguageService.getInstance();
        initializePsalms();
        loadHighlights();
    }

    public static PsalmService getInstance() {
        if (instance == null) {
            instance = new PsalmService();
        }
        return instance;
    }

    public List<Psalm> getAllPsalms() {
        return new ArrayList<>(allPsalms);
    }

    public Psalm getPsalmByNumber(int number) {
        return allPsalms.stream()
                .filter(p -> p.getNumber() == number)
                .findFirst()
                .orElse(null);
    }

    public List<Psalm> searchPsalmsByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return allPsalms.stream()
                .filter(p -> {
                    String title = getPsalmTitle(p, languageService.getCurrentLanguage()).toLowerCase();
                    String text = getPsalmText(p, languageService.getCurrentLanguage()).toLowerCase();
                    return title.contains(lowerKeyword) || text.contains(lowerKeyword);
                })
                .collect(Collectors.toList());
    }

    public String getPsalmTitle(Psalm psalm, String language) {
        String key = String.format("psalm_%d_title", psalm.getNumber());
        String translation = languageService.getTranslation(key, language);

        if (translation.equals(key)) {
            return String.format("Psalm %d", psalm.getNumber());
        }
        return translation;
    }

    public String getPsalmText(Psalm psalm, String language) {
        String key = String.format("psalm_%d", psalm.getNumber());
        return languageService.getTranslation(key, language);
    }

    public void addToFavorites(Psalm psalm) {
        favoritePsalmNumbers.add(psalm.getNumber());
        psalm.setFavorite(true);
    }

    public void removeFromFavorites(Psalm psalm) {
        favoritePsalmNumbers.remove(psalm.getNumber());
        psalm.setFavorite(false);
    }

    public List<Psalm> getFavoritePsalms() {
        return allPsalms.stream()
                .filter(p -> favoritePsalmNumbers.contains(p.getNumber()))
                .peek(p -> p.setFavorite(true))
                .collect(Collectors.toList());
    }

    // ==================== HIGHLIGHT FEATURE METHODS ====================

    /**
     * Add a highlight to a specific psalm
     * @param psalmNumber The psalm number
     * @param startIndex Start position of highlight
     * @param endIndex End position of highlight
     * @param color Highlight color
     */
    public void addHighlight(int psalmNumber, int startIndex, int endIndex, Color color) {
        String psalmKey = String.valueOf(psalmNumber);

        if (!highlightsByPsalm.containsKey(psalmKey)) {
            highlightsByPsalm.put(psalmKey, new ArrayList<>());
        }

        HighlightData highlight = new HighlightData(startIndex, endIndex, color);
        highlightsByPsalm.get(psalmKey).add(highlight);

        saveHighlights();
    }

    /**
     * Remove a specific highlight from a psalm
     * @param psalmNumber The psalm number
     * @param startIndex Start position of highlight to remove
     * @param endIndex End position of highlight to remove
     */
    public void removeHighlight(int psalmNumber, int startIndex, int endIndex) {
        String psalmKey = String.valueOf(psalmNumber);

        if (highlightsByPsalm.containsKey(psalmKey)) {
            List<HighlightData> highlights = highlightsByPsalm.get(psalmKey);
            highlights.removeIf(h -> h.startIndex == startIndex && h.endIndex == endIndex);

            if (highlights.isEmpty()) {
                highlightsByPsalm.remove(psalmKey);
            }

            saveHighlights();
        }
    }

    /**
     * Clear all highlights for a specific psalm
     * @param psalmNumber The psalm number
     */
    public void clearAllHighlights(int psalmNumber) {
        String psalmKey = String.valueOf(psalmNumber);
        highlightsByPsalm.remove(psalmKey);
        saveHighlights();
    }

    /**
     * Get all highlights for a specific psalm
     * @param psalmNumber The psalm number
     * @return List of highlights
     */
    public List<HighlightData> getHighlights(int psalmNumber) {
        String psalmKey = String.valueOf(psalmNumber);
        return highlightsByPsalm.getOrDefault(psalmKey, new ArrayList<>());
    }

    /**
     * Check if a psalm has any highlights
     * @param psalmNumber The psalm number
     * @return true if psalm has highlights
     */
    public boolean hasHighlights(int psalmNumber) {
        String psalmKey = String.valueOf(psalmNumber);
        return highlightsByPsalm.containsKey(psalmKey) &&
                !highlightsByPsalm.get(psalmKey).isEmpty();
    }

    /**
     * Save highlights to file
     */
    private void saveHighlights() {
        try {
            File file = new File(HIGHLIGHTS_FILE);
            file.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(highlightsByPsalm);
            }

            System.out.println("Highlights saved successfully");

        } catch (IOException e) {
            System.err.println("Error saving highlights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load highlights from file
     */
    @SuppressWarnings("unchecked")
    private void loadHighlights() {
        File file = new File(HIGHLIGHTS_FILE);

        if (!file.exists()) {
            System.out.println("No saved highlights found");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highlightsByPsalm = (Map<String, List<HighlightData>>) ois.readObject();
            System.out.println("Highlights loaded successfully: " + highlightsByPsalm.size() + " psalms with highlights");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading highlights: " + e.getMessage());
            highlightsByPsalm = new HashMap<>();
        }
    }

    // ==================== HIGHLIGHT DATA CLASS ====================

    /**
     * Data class for storing highlight information
     */
    public static class HighlightData implements Serializable {
        private static final long serialVersionUID = 1L;

        private int startIndex;
        private int endIndex;
        private int colorRGB;

        public HighlightData(int startIndex, int endIndex, Color color) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.colorRGB = color.getRGB();
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public Color getColor() {
            return new Color(colorRGB);
        }

        public int getColorRGB() {
            return colorRGB;
        }

        @Override
        public String toString() {
            return String.format("Highlight[%d-%d, color=%d]", startIndex, endIndex, colorRGB);
        }
    }

    private void initializePsalms() {
        allPsalms = new ArrayList<>();

        String[] commonPsalmTitles = {
                "The Lord is My Shepherd",
                "Create in Me a Clean Heart",
                "Praise the Lord",
                "Out of the Depths",
                "The Lord is My Light",
                "Give Thanks to the Lord",
                "Bless the Lord, O My Soul",
                "How Lovely is Your Dwelling Place",
                "The Earth is the Lord's",
                "I Will Lift Up My Eyes"
        };

        int[] psalmNumbers = {23, 51, 150, 130, 27, 136, 103, 84, 24, 121};

        for (int i = 0; i < psalmNumbers.length; i++) {
            allPsalms.add(new Psalm(psalmNumbers[i], commonPsalmTitles[i]));
        }

        for (int i = 1; i <= 150; i++) {
            final int psalmNum = i;
            if (Arrays.stream(psalmNumbers).noneMatch(x -> x == psalmNum)) {
                allPsalms.add(new Psalm(psalmNum, String.format("Psalm %d", psalmNum)));
            }
        }

        allPsalms.sort(Comparator.comparingInt(Psalm::getNumber));
    }
}