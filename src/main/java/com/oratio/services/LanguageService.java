package com.oratio.services;

import com.oratio.utils.Constants;
import com.oratio.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling multi-language support
 */
public class LanguageService {
    private static LanguageService instance;
    private String currentLanguage = Constants.DEFAULT_LANGUAGE;
    private Map<String, Map<String, String>> translations = new HashMap<>();

    private LanguageService() {
        loadTranslations();
    }

    public static LanguageService getInstance() {
        if (instance == null) {
            instance = new LanguageService();
        }
        return instance;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(String language) {
        if (isLanguageSupported(language)) {
            this.currentLanguage = language;
        }
    }

    public boolean isLanguageSupported(String language) {
        for (String supportedLang : Constants.SUPPORTED_LANGUAGES) {
            if (supportedLang.equals(language)) {
                return true;
            }
        }
        return false;
    }

    public String getTranslation(String key, String language) {
        Map<String, String> langTranslations = translations.get(language);
        if (langTranslations != null && langTranslations.containsKey(key)) {
            return langTranslations.get(key);
        }

        // Fallback to English if translation not found
        Map<String, String> englishTranslations = translations.get(Constants.ENGLISH);
        if (englishTranslations != null && englishTranslations.containsKey(key)) {
            return englishTranslations.get(key);
        }

        return key; // Return key if no translation found
    }

    public String getTranslation(String key) {
        return getTranslation(key, currentLanguage);
    }

    private void loadTranslations() {
        for (String language : Constants.SUPPORTED_LANGUAGES) {
            Map<String, String> langTranslations = new HashMap<>();

            // Load prayers
            String prayersContent = FileUtils.loadResourceFile(String.format("prayers_%s.txt", language.toLowerCase()));
            if (prayersContent != null) {
                parseTranslations(prayersContent, langTranslations);
            }

            // Load rosary texts
            String rosaryContent = FileUtils.loadResourceFile(String.format("rosary_%s.txt", language.toLowerCase()));
            if (rosaryContent != null) {
                parseTranslations(rosaryContent, langTranslations);
            }

            // Load psalms
            String psalmsContent = FileUtils.loadResourceFile(String.format("psalms_%s.txt", language.toLowerCase()));
            if (psalmsContent != null) {
                parseTranslations(psalmsContent, langTranslations);
            }

            translations.put(language, langTranslations);
        }
    }

    private void parseTranslations(String content, Map<String, String> translations) {
        if (content == null || content.trim().isEmpty()) return;

        String[] sections = content.split("===");
        for (String section : sections) {
            String[] lines = section.trim().split("\n", 2);
            if (lines.length >= 2) {
                String key = lines[0].trim();
                String value = lines[1].trim();
                translations.put(key, value);
            }
        }
    }
}