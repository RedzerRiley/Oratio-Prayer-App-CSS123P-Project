
// File: src/main/java/com/oratio/services/PrayerService.java
package com.oratio.services;

import com.oratio.models.Prayer;
import com.oratio.utils.FileUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Service for managing prayers and devotions
 */
public class PrayerService {
    private static PrayerService instance;
    private List<Prayer> allPrayers = new ArrayList<>();
    private Set<String> favoritePrayerIds = new HashSet<>();
    private Set<String> completedPrayerIds = new HashSet<>();
    private LanguageService languageService;

    // Cache for loaded prayer texts by language
    private Map<String, Map<String, String>> prayerTextCache = new HashMap<>();

    private PrayerService() {
        languageService = LanguageService.getInstance();
        initializePrayers();
        loadAllPrayerTexts();
    }

    public static PrayerService getInstance() {
        if (instance == null) {
            instance = new PrayerService();
        }
        return instance;
    }

    public List<Prayer> getDailyPrayers() {
        List<Prayer> dailyPrayers = new ArrayList<>();

        // Add morning prayers
        dailyPrayers.add(new Prayer("morning_offering", "Morning Offering", "Morning"));
        dailyPrayers.add(new Prayer("our_father", "Our Father", "Basic"));
        dailyPrayers.add(new Prayer("hail_mary", "Hail Mary", "Basic"));
        dailyPrayers.add(new Prayer("glory_be", "Glory Be", "Basic"));

        // Add evening prayers
        dailyPrayers.add(new Prayer("evening_prayer", "Evening Prayer", "Evening"));
        dailyPrayers.add(new Prayer("act_of_contrition", "Act of Contrition", "Evening"));

        return dailyPrayers;
    }

    /**
     * Get prayer text in the specified language
     * This method retrieves the prayer text from the cache loaded via FileUtils
     */
    public String getPrayerText(Prayer prayer, String language) {
        // Get the cached prayer texts for this language
        Map<String, String> languagePrayers = prayerTextCache.get(language);

        if (languagePrayers != null && languagePrayers.containsKey(prayer.getId())) {
            return languagePrayers.get(prayer.getId());
        }

        // Fallback: try to load from LanguageService or return a default message
        String text = languageService.getTranslation(prayer.getId(), language);

        // If still not found, return a helpful message
        if (text.equals(prayer.getId())) {
            return "Prayer text not available.\n\nPlease ensure the prayer file for " +
                    language + " is properly configured.";
        }

        return text;
    }

    /**
     * Load all prayer texts from FileUtils for all supported languages
     */
    private void loadAllPrayerTexts() {
        String[] languages = {"English", "Tagalog", "Latin"};

        for (String language : languages) {
            loadPrayerTextsForLanguage(language);
        }
    }

    /**
     * Load prayer texts for a specific language from FileUtils
     */
    private void loadPrayerTextsForLanguage(String language) {
        String fileName = "prayers_" + language.toLowerCase() + ".txt";
        String content = FileUtils.loadResourceFile(fileName);

        Map<String, String> prayerTexts = parsePrayerContent(content);
        prayerTextCache.put(language, prayerTexts);

        System.out.println("Loaded " + prayerTexts.size() + " prayers for " + language);
    }

    /**
     * Parse prayer content from the loaded file
     * Format: prayer_id followed by prayer text, separated by ===
     */
    private Map<String, String> parsePrayerContent(String content) {
        Map<String, String> prayers = new HashMap<>();

        if (content == null || content.trim().isEmpty()) {
            return prayers;
        }

        // Split by === delimiter
        String[] sections = content.split("===");

        for (String section : sections) {
            section = section.trim();
            if (section.isEmpty()) {
                continue;
            }

            // Split into lines
            String[] lines = section.split("\n", 2);

            if (lines.length >= 2) {
                String prayerId = lines[0].trim();
                String prayerText = lines[1].trim();
                prayers.put(prayerId, prayerText);
            } else if (lines.length == 1) {
                // If only one line, it might be just the ID
                String prayerId = lines[0].trim();
                prayers.put(prayerId, "");
            }
        }

        return prayers;
    }

    /**
     * Reload prayer texts (useful when language files are updated)
     */
    public void reloadPrayerTexts() {
        prayerTextCache.clear();
        loadAllPrayerTexts();
    }

    /**
     * Get all available prayer IDs for a specific language
     */
    public Set<String> getAvailablePrayerIds(String language) {
        Map<String, String> languagePrayers = prayerTextCache.get(language);
        return languagePrayers != null ? languagePrayers.keySet() : new HashSet<>();
    }

    public void markAsCompleted(Prayer prayer) {
        completedPrayerIds.add(prayer.getId());
        prayer.setCompleted(true);
    }

    public void addToFavorites(Prayer prayer) {
        favoritePrayerIds.add(prayer.getId());
        prayer.setFavorite(true);
    }

    public void removeFromFavorites(Prayer prayer) {
        favoritePrayerIds.remove(prayer.getId());
        prayer.setFavorite(false);
    }

    public List<Prayer> getFavoritePrayers() {
        List<Prayer> favorites = new ArrayList<>();
        for (Prayer prayer : allPrayers) {
            if (favoritePrayerIds.contains(prayer.getId())) {
                prayer.setFavorite(true);
                favorites.add(prayer);
            }
        }
        return favorites;
    }

    private void initializePrayers() {
        allPrayers.addAll(getDailyPrayers());

        // Add special devotions
        allPrayers.add(new Prayer("angelus", "The Angelus", "Devotion"));
        allPrayers.add(new Prayer("divine_mercy", "Divine Mercy Chaplet", "Devotion"));
        allPrayers.add(new Prayer("stations_cross", "Stations of the Cross", "Devotion"));
    }
}