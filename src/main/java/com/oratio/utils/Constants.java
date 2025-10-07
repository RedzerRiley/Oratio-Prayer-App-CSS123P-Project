

// File: src/main/java/com/oratio/utils/Constants.java
package com.oratio.utils;

/**
 * Application constants
 */
public class Constants {
    // Supported languages
    public static final String ENGLISH = "English";
    public static final String TAGALOG = "Tagalog";
    public static final String LATIN = "Latin";

    public static final String[] SUPPORTED_LANGUAGES = {ENGLISH, TAGALOG, LATIN};
    public static final String DEFAULT_LANGUAGE = ENGLISH;

    // Application info
    public static final String APP_NAME = "Oratio";
    public static final String APP_SUBTITLE = "A Catholic Prayer App";
    public static final String VERSION = "1.0.0";

    // File paths for prayer texts
    public static final String RESOURCES_PATH = "src/main/resources/";
    public static final String PRAYERS_PATH = RESOURCES_PATH + "prayers/";
    public static final String ROSARY_PATH = RESOURCES_PATH + "rosary/";
    public static final String PSALMS_PATH = RESOURCES_PATH + "psalms/";
    public static final String NOVENAS_PATH = RESOURCES_PATH + "novenas/";

    // Prayer categories
    public static final String CATEGORY_MORNING = "Morning";
    public static final String CATEGORY_EVENING = "Evening";
    public static final String CATEGORY_BASIC = "Basic";
    public static final String CATEGORY_DEVOTION = "Devotion";

    // Note categories
    public static final String[] NOTE_CATEGORIES = {
            "Prayer", "Reflection", "Intention", "Gratitude", "Other"
    };

    // UI Constants
    public static final int DEFAULT_WINDOW_WIDTH = 800;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;
    public static final int SPLIT_PANE_DIVIDER_LOCATION = 300;

    private Constants() {
        // Utility class - private constructor
    }
}


