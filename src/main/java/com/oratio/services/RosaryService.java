package com.oratio.services;

// File: src/main/java/com/oratio/services/RosaryService.java

import com.oratio.models.RosaryMystery;
import com.oratio.models.RosaryStep;

import java.time.LocalDate;
import java.util.*;

/**
 * Service for managing rosary mysteries and prayers
 */
public class RosaryService {
    private static RosaryService instance;
    private List<RosaryMystery> mysteries;
    private LanguageService languageService;

    private RosaryService() {
        languageService = LanguageService.getInstance();
        initializeMysteries();
    }

    public static RosaryService getInstance() {
        if (instance == null) {
            instance = new RosaryService();
        }
        return instance;
    }

    public RosaryMystery getTodaysMystery() {
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

        // Traditional rosary schedule
        switch (dayOfWeek) {
            case 1: // Monday
            case 6: // Saturday
                return getMysteryByType(RosaryMystery.MysteryType.JOYFUL);
            case 2: // Tuesday
            case 5: // Friday
                return getMysteryByType(RosaryMystery.MysteryType.SORROWFUL);
            case 3: // Wednesday
            case 7: // Sunday
                return getMysteryByType(RosaryMystery.MysteryType.GLORIOUS);
            case 4: // Thursday
                return getMysteryByType(RosaryMystery.MysteryType.LUMINOUS);
            default:
                return getMysteryByType(RosaryMystery.MysteryType.JOYFUL);
        }
    }

    public List<RosaryMystery> getAllMysteries() {
        return new ArrayList<>(mysteries);
    }

    private RosaryMystery getMysteryByType(RosaryMystery.MysteryType type) {
        return mysteries.stream()
                .filter(m -> m.getType() == type)
                .findFirst()
                .orElse(mysteries.get(0));
    }

    public String getMysteryTitle(RosaryMystery mystery, String language) {
        return languageService.getTranslation(" " + mystery.getId(), language);
    }

    public List<RosaryStep> getRosarySteps(RosaryMystery mystery) {
        List<RosaryStep> steps = new ArrayList<>();

        // Opening prayers
        steps.add(new RosaryStep("sign_cross", "Sign of the Cross", "sign_cross", 1));
        steps.add(new RosaryStep("apostles_creed", "Apostles' Creed", "apostles_creed", 1));
        steps.add(new RosaryStep("our_father_1", "Our Father", "our_father", 1));

        // Three Hail Marys for Faith, Hope, and Charity
        steps.add(new RosaryStep("hail_mary_1", "First Hail Mary (Faith)", "hail_mary", 1));
        steps.add(new RosaryStep("hail_mary_2", "Second Hail Mary (Hope)", "hail_mary", 1));
        steps.add(new RosaryStep("hail_mary_3", "Third Hail Mary (Charity)", "hail_mary", 1));
        steps.add(new RosaryStep("glory_be_1", "Glory Be", "glory_be", 1));

        // Five decades of the rosary
        for (int decade = 1; decade <= 5; decade++) {
            String mysteryMeditation = getMysteryMeditation(mystery, decade);
            steps.add(new RosaryStep("mystery_" + decade,
                    String.format("%d. %s", decade, getMysteryName(mystery, decade)),
                    "", 1, mysteryMeditation));
            steps.add(new RosaryStep("our_father_" + (decade + 1), "Our Father", "our_father", 1));
            steps.add(new RosaryStep("hail_mary_decade_" + decade, "Ten Hail Marys", "hail_mary", 10));
            steps.add(new RosaryStep("glory_be_" + (decade + 1), "Glory Be", "glory_be", 1));
            steps.add(new RosaryStep("fatima_prayer_" + decade, "Fatima Prayer", "fatima_prayer", 1));
        }

        // Closing prayers
        steps.add(new RosaryStep("hail_holy_queen", "Hail Holy Queen", "hail_holy_queen", 1));
        steps.add(new RosaryStep("final_prayer", "Final Prayer", "rosary_final_prayer", 1));
        steps.add(new RosaryStep("sign_cross_final", "Sign of the Cross", "sign_cross", 1));

        return steps;
    }

    public String getStepText(RosaryStep step, String language) {
        String baseText = languageService.getTranslation(step.getPrayerType(), language);

        if (step.getMeditation() != null && !step.getMeditation().isEmpty()) {
            String meditation = languageService.getTranslation(step.getMeditation(), language);
            return  meditation+ "\n\n" + baseText;
        }

        if (step.getRepetitions() > 1) {
            return baseText + "\n\n(Repeat " + step.getRepetitions() + " times)";
        }

        return baseText;
    }

    private String getMysteryName(RosaryMystery mystery, int decade) {
        String key = String.format("%s_mystery_%d", mystery.getId(), decade);
        return languageService.getTranslation(key);
    }

    private String getMysteryMeditation(RosaryMystery mystery, int decade) {
        return "Meditate and reflect upon this mystery"; // always use the same translation key
    }


    private void initializeMysteries() {
        mysteries = new ArrayList<>();

        mysteries.add(new RosaryMystery("joyful", "Joyful Mysteries",
                RosaryMystery.MysteryType.JOYFUL, new String[]{"Monday", "Saturday"}));
        mysteries.add(new RosaryMystery("sorrowful", "Sorrowful Mysteries",
                RosaryMystery.MysteryType.SORROWFUL, new String[]{"Tuesday", "Friday"}));
        mysteries.add(new RosaryMystery("glorious", "Glorious Mysteries",
                RosaryMystery.MysteryType.GLORIOUS, new String[]{"Wednesday", "Sunday"}));
        mysteries.add(new RosaryMystery("luminous", "Luminous Mysteries",
                RosaryMystery.MysteryType.LUMINOUS, new String[]{"Thursday"}));
    }
}



// File: src/main/java/com/oratio/services/NovenaService.java


// File: src/main/java/com/oratio/services/NotesService.java
