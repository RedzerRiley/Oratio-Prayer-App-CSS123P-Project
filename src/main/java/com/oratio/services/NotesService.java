// File: src/main/java/com/oratio/services/NotesService.java
package com.oratio.services;

import com.oratio.models.Note;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing prayer journal notes
 */
public class NotesService {
    private static NotesService instance;
    private List<Note> allNotes = new ArrayList<>();
    private int nextNoteId = 1;

    private NotesService() {
        initializeDefaultNotes();

    }

    public static NotesService getInstance() {
        if (instance == null) {
            instance = new NotesService();
        }
        return instance;
    }

    public List<Note> getAllNotes() {
        // Sort by modification date, newest first
        List<Note> sortedNotes = new ArrayList<>(allNotes);
        sortedNotes.sort((n1, n2) -> n2.getModifiedDate().compareTo(n1.getModifiedDate()));
        return sortedNotes;
    }

    public Note createNote(String title, String content, String category) {
        Note note = new Note(String.valueOf(nextNoteId++), title, content, category);
        allNotes.add(note);
        return note;
    }

    public void saveNote(Note note) {
        // In a real application, this would persist to file/database
        // For now, the note is already in the list and modifications are tracked
        note.setModifiedDate(LocalDateTime.now());
    }

    public void deleteNote(Note note) {
        allNotes.remove(note);
    }

    public List<Note> getNotesByCategory(String category) {
        return allNotes.stream()
                .filter(note -> note.getCategory().equals(category))
                .sorted((n1, n2) -> n2.getModifiedDate().compareTo(n1.getModifiedDate()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private void initializeDefaultNotes() {
        // Create a welcome note
        Note welcomeNote = createNote("Welcome to Oratio",
                "Welcome to your prayer journal!\n\n" +
                        "This space is for your personal reflections, prayer intentions, " +
                        "and spiritual insights. You can organize your notes by categories " +
                        "like Prayer, Reflection, Intention, Gratitude, or Other.\n\n" +
                        "May this digital companion support you in your spiritual journey.",
                "Other");

        // Create a sample prayer intention note
        Note intentionNote = createNote("Prayer Intentions",
                "Today I pray for:\n\n" +
                        "• My family's health and wellbeing\n" +
                        "• Peace in our troubled world\n" +
                        "• Those who are suffering\n" +
                        "• Guidance in my spiritual journey\n\n" +
                        "Lord, hear our prayers.",
                "Intention");

        // Create a sample gratitude note
        Note gratitudeNote = createNote("Daily Gratitude",
                "Things I'm grateful for today:\n\n" +
                        "• The gift of life and health\n" +
                        "• My loved ones\n" +
                        "• God's constant presence\n" +
                        "• The beauty of creation\n\n" +
                        "Thank you, Lord, for all your blessings.",
                "Gratitude");
    }

}
//}n1.getModifiedDate()))
//        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
//    }
//
//public List<Note> searchNotes(String searchTerm) {
//    String lowerSearchTerm = searchTerm.toLowerCase();
//    return allNotes.stream()
//            .filter(note -> note.getTitle().toLowerCase().contains(lowerSearchTerm) ||
//                    note.getContent().toLowerCase().contains(lowerSearchTerm))
//            .sorted((n1, n2) -> n2.getModifiedDate().compareTo(