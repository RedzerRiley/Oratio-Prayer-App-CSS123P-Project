package com.oratio.services;

import com.oratio.models.Novena;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.*;

/**
 * Service for managing novenas
 */
public class NovenaService {
    private static NovenaService instance;
    private List<Novena> allNovenas;
    private Set<String> favoriteNovenaIds = new HashSet<>();
    private Set<String> activeNovenaIds = new HashSet<>();
    private LanguageService languageService;

    private NovenaService() {
        languageService = LanguageService.getInstance();
        initializeNovenas();
    }

    private String loadResourceFile(String path) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                return "Resource not found: " + path;
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading resource: " + path;
        }
    }


    public static NovenaService getInstance() {
        if (instance == null) {
            instance = new NovenaService();
        }
        return instance;
    }

    public List<Novena> getAllNovenas() {
        return new ArrayList<>(allNovenas);
    }

    public String getNovenaText(Novena novena, String language) {
        // Example filename: sacred_heart_english.txt
        String filename = String.format("novenas/%s_%s.txt",
                novena.getId().toLowerCase(),
                language.toLowerCase());

        return loadResourceFile(filename);
    }




    public void addToFavorites(Novena novena) {
        favoriteNovenaIds.add(novena.getId());
        novena.setFavorite(true);
    }

    public void removeFromFavorites(Novena novena) {
        favoriteNovenaIds.remove(novena.getId());
        novena.setFavorite(false);
    }

    public List<Novena> getFavoriteNovenas() {
        return allNovenas.stream()
                .filter(n -> favoriteNovenaIds.contains(n.getId()))
                .peek(n -> n.setFavorite(true))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void startNovena(Novena novena) {
        activeNovenaIds.add(novena.getId());
        // In a real app, this would create a tracking system for the 9-day period
        NotesService notesService = NotesService.getInstance();
        notesService.createNote(
                String.format("%s - Day 1", novena.getName()),
                String.format("Started novena: %s\n\n%s", novena.getName(), novena.getDescription()),
                "Novena"
        );
    }

    public boolean isNovenaActive(Novena novena) {
        return activeNovenaIds.contains(novena.getId());
    }

    private void initializeNovenas() {
        allNovenas = new ArrayList<>();

        allNovenas.add(new Novena("sacred_heart", "Sacred Heart of Jesus",
                "A powerful novena to the Sacred Heart of Jesus for spiritual and temporal needs", 9));

        allNovenas.add(new Novena("st_carlo", "St. Carlo Acutis",
                "Novena to St. Carlo Acutis, the Patron Saint of programmers ", 9 ));

        allNovenas.add(new Novena("perpetual_help", "Our Lady of Perpetual Help",
                "Novena to Our Lady of Perpetual Help for intercession and protection", 9));

        allNovenas.add(new Novena("divine_mercy", "Divine Mercy",
                "The Divine Mercy Novena typically prayed before Divine Mercy Sunday", 9));

        allNovenas.add(new Novena("st_jude", "St. Jude Thaddeus",
                "Novena to St. Jude, patron saint of hopeless cases", 9));

        allNovenas.add(new Novena("immaculate_heart", "Immaculate Heart of Mary",
                "Novena to the Immaculate Heart of Mary for peace and conversion", 9));

        allNovenas.add(new Novena("st_anthony", "St. Anthony of Padua",
                "Novena to St. Anthony for help in finding lost things and guidance", 9));

        allNovenas.add(new Novena("holy_spirit", "Holy Spirit",
                "Novena to the Holy Spirit for wisdom, guidance, and spiritual gifts", 9));

        allNovenas.add(new Novena("st_therese", "St. Thérèse of Lisieux",
                "Novena to St. Thérèse, the Little Flower, for intercession and roses", 9));


    }
}