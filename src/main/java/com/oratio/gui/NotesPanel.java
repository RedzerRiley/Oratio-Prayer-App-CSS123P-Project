// File: src/main/java/com/oratio/gui/NotesPanel.java
package com.oratio.gui;

import com.oratio.models.Note;
import com.oratio.models.Novena;
import com.oratio.services.NotesService;
import com.oratio.services.ThemeService;
import com.oratio.utils.ModernUIUtils;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotesPanel extends JPanel {
    private JList<Note> notesList;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton newButton;
    private JButton deleteButton;
    private JComboBox<String> categoryCombo;
    private JLabel dateLabel;

    private NotesService notesService;
    private Note currentNote;

    public NotesPanel() {
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        loadNotes();
    }

    private void initializeServices() {
        notesService = NotesService.getInstance();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeService.getInstance().getBackgroundColor());

        // Create split pane with proper sizing
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.35); // Left panel gets 35% of space

        // Left panel - notes list
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setMinimumSize(new Dimension(250, 0));

        JLabel leftTitle = new JLabel("Prayer Journal");
        leftTitle.setFont(ThemeService.getInstance().getHeadingFont());
        leftTitle.setForeground(ThemeService.getInstance().getForegroundColor());
        leftPanel.add(leftTitle, BorderLayout.NORTH);

        notesList = new JList<>();
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.setCellRenderer(new ModernNotesListRenderer());
        notesList.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        notesList.setForeground(ThemeService.getInstance().getForegroundColor());
        notesList.setSelectionBackground(ThemeService.getInstance().getAccentColor());
        notesList.setSelectionForeground(Color.WHITE);

        JScrollPane listScrollPane = new JScrollPane(notesList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()));
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        newButton = new JButton("New Note");
        deleteButton = new JButton("Delete");
        styleButton(newButton, true);
        styleButton(deleteButton, false);
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // Right panel - note editor
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel rightTitle = new JLabel("Note Editor");
        rightTitle.setFont(ThemeService.getInstance().getHeadingFont());
        rightTitle.setForeground(ThemeService.getInstance().getForegroundColor());
        rightPanel.add(rightTitle, BorderLayout.NORTH);

        // Header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        headerPanel.add(titleLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField();
        styleTextField(titleField);
        headerPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        headerPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        categoryCombo = new JComboBox<>(new String[]{"Prayer", "Reflection", "Intention", "Gratitude", "Other"});
        styleComboBox(categoryCombo);
        headerPanel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dateLabel.setForeground(ThemeService.getInstance().getSecondaryTextColor());
        headerPanel.add(dateLabel, gbc);

        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area
        contentArea = new JTextArea();
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        contentArea.setForeground(ThemeService.getInstance().getForegroundColor());
        contentArea.setCaretColor(ThemeService.getInstance().getForegroundColor());

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()));
        rightPanel.add(contentScrollPane, BorderLayout.CENTER);

        // Save button
        JPanel savePanel = new JPanel(new FlowLayout());
        savePanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        saveButton = new JButton("Save Note");
        styleButton(saveButton, true);
        savePanel.add(saveButton);
        rightPanel.add(savePanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(320);

        add(splitPane, BorderLayout.CENTER);
        setEditorEnabled(false);
    }

    private void styleButton(JButton button, boolean isPrimary) {
        ThemeService theme = ThemeService.getInstance();
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        if (isPrimary) {
            button.setBackground(theme.getAccentColor());
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(theme.getBorderColor());
            button.setForeground(theme.getForegroundColor());
        }

        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void styleTextField(JTextField textField) {
        ThemeService theme = ThemeService.getInstance();
        textField.setBackground(theme.getCardBackgroundColor());
        textField.setForeground(theme.getForegroundColor());
        textField.setCaretColor(theme.getForegroundColor());
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        ThemeService theme = ThemeService.getInstance();
        comboBox.setBackground(theme.getCardBackgroundColor());
        comboBox.setForeground(theme.getForegroundColor());
        comboBox.setBorder(BorderFactory.createLineBorder(theme.getBorderColor()));
    }

    private void setupEventHandlers() {
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Note selectedNote = notesList.getSelectedValue();
                if (selectedNote != null) {
                    loadNoteIntoEditor(selectedNote);
                }
            }
        });

        newButton.addActionListener(e -> createNewNote());
        deleteButton.addActionListener(e -> deleteSelectedNote());
        saveButton.addActionListener(e -> saveCurrentNote());
    }

    private void loadNotes() {
        List<Note> notes = notesService.getAllNotes();
        DefaultListModel<Note> model = new DefaultListModel<>();
        for (Note note : notes) {
            model.addElement(note);
        }
        notesList.setModel(model);

        if (!notes.isEmpty()) {
            notesList.setSelectedIndex(0);
        }
    }

    private void createNewNote() {
        String title = JOptionPane.showInputDialog(this, "Enter note title:", "New Note", JOptionPane.PLAIN_MESSAGE);
        if (title != null && !title.trim().isEmpty()) {
            Note newNote = notesService.createNote(title.trim(), "", "Prayer");
            loadNotes();
            notesList.setSelectedValue(newNote, true);
            setEditorEnabled(true);
            titleField.requestFocus();
        }
    }

    private void deleteSelectedNote() {
        Note selectedNote = notesList.getSelectedValue();
        if (selectedNote != null) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this note?",
                    "Delete Note",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                notesService.deleteNote(selectedNote);
                loadNotes();
                setEditorEnabled(false);
                clearEditor();
            }
        }
    }

    private void saveCurrentNote() {
        if (currentNote != null) {
            updateCurrentNoteFromEditor();
            notesService.saveNote(currentNote);
            loadNotes();
            notesList.setSelectedValue(currentNote, true);
            JOptionPane.showMessageDialog(this, "Note saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadNoteIntoEditor(Note note) {
        currentNote = note;
        titleField.setText(note.getTitle());
        contentArea.setText(note.getContent());
        categoryCombo.setSelectedItem(note.getCategory());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        dateLabel.setText("Created: " + note.getCreatedDate().format(formatter) +
                " | Modified: " + note.getModifiedDate().format(formatter));

        setEditorEnabled(true);
    }

    private void updateCurrentNoteFromEditor() {
        if (currentNote != null) {
            currentNote.setTitle(titleField.getText().trim());
            currentNote.setContent(contentArea.getText());
            currentNote.setCategory((String) categoryCombo.getSelectedItem());
        }
    }

    private void clearEditor() {
        titleField.setText("");
        contentArea.setText("");
        categoryCombo.setSelectedIndex(0);
        dateLabel.setText("");
        currentNote = null;
    }

    private void setEditorEnabled(boolean enabled) {
        titleField.setEnabled(enabled);
        contentArea.setEnabled(enabled);
        categoryCombo.setEnabled(enabled);
        saveButton.setEnabled(enabled);
    }

    public void refreshContent() {
        // Apply current theme
        ThemeService theme = ThemeService.getInstance();
        setBackground(theme.getBackgroundColor());
        theme.applyTheme(this);

        loadNotes();
        if (currentNote != null) {
            loadNoteIntoEditor(currentNote);
        }
    }

    private static class ModernNotesListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout(8, 4));
            ThemeService theme = ThemeService.getInstance();

            panel.setBackground(isSelected ? theme.getAccentColor() : theme.getCardBackgroundColor());
            panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            if (value instanceof Note) {
                Note note = (Note) value;

                JLabel titleLabel = new JLabel(note.getTitle());
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                titleLabel.setForeground(isSelected ? Color.WHITE : theme.getForegroundColor());

                JLabel categoryLabel = new JLabel(note.getCategory());
                categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                categoryLabel.setForeground(isSelected ? new Color(240, 240, 240) : theme.getSecondaryTextColor());

                panel.add(titleLabel, BorderLayout.NORTH);
                panel.add(categoryLabel, BorderLayout.SOUTH);
            }

            return panel;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 45);
        }

    }
}