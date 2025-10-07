// File: src/main/java/com/oratio/services/PsalmService.java
package com.oratio.services;

import com.oratio.models.Psalm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing psalms
 */
public class PsalmService {
    private static PsalmService instance;
    private List<Psalm> allPsalms;
    private Set<Integer> favoritePsalmNumbers = new HashSet<>();
    private LanguageService languageService;

    private PsalmService() {
        languageService = LanguageService.getInstance();
        initializePsalms();
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

        // If no specific title translation, use default format
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

    private void initializePsalms() {
        allPsalms = new ArrayList<>();

        // Initialize with common psalms (in a real app, this would load from files)
        String[] commonPsalmTitles = {
                "The Lord is My Shepherd", // Psalm 23
                "Create in Me a Clean Heart", // Psalm 51
                "Praise the Lord", // Psalm 150
                "Out of the Depths", // Psalm 130
                "The Lord is My Light", // Psalm 27
                "Give Thanks to the Lord", // Psalm 136
                "Bless the Lord, O My Soul", // Psalm 103
                "How Lovely is Your Dwelling Place", // Psalm 84
                "The Earth is the Lord's", // Psalm 24
                "I Will Lift Up My Eyes" // Psalm 121
        };

        int[] psalmNumbers = {23, 51, 150, 130, 27, 136, 103, 84, 24, 121};

        for (int i = 0; i < psalmNumbers.length; i++) {
            allPsalms.add(new Psalm(psalmNumbers[i], commonPsalmTitles[i]));
        }

        // Add more psalms (1-150)
        for (int i = 1; i <= 150; i++) {
            final int psalmNum = i; // make a final copy for the lambda
            if (Arrays.stream(psalmNumbers).noneMatch(x -> x == psalmNum)) {
                allPsalms.add(new Psalm(psalmNum, String.format("Psalm %d", psalmNum)));
            }
        }


        // Sort by psalm number
        allPsalms.sort(Comparator.comparingInt(Psalm::getNumber));
    }
}