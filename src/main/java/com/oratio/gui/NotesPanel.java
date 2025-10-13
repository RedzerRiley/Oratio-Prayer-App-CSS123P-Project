// File: src/main/java/com/oratio/gui/NotesPanel.java
package com.oratio.gui;

import com.oratio.models.Note;
import com.oratio.models.Novena;
import com.oratio.services.NotesService;
import com.oratio.services.ThemeService;
import com.oratio.utils.ModernUIUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotesPanel extends JPanel {
    private JList<Note> notesList;
    private JTextField titleField;
    private JTextPane contentArea;
    private JButton saveButton;
    private JButton newButton;
    private JButton deleteButton;
    private ButtonGroup categoryGroup;
    private JRadioButton prayerRadio;
    private JRadioButton reflectionRadio;
    private JRadioButton intentionRadio;
    private JRadioButton gratitudeRadio;
    private JRadioButton otherRadio;
    private JLabel dateLabel;

    // Formatting controls
    private JRadioButton boldRadio;
    private JRadioButton italicRadio;
    private JRadioButton normalRadio;
    private ButtonGroup styleGroup;
    private JTextField fontSizeField;
    private JButton applyFontSizeButton;

    private NotesService notesService;
    private Note currentNote;

    // Track current formatting for new text
    private boolean currentBold = false;
    private boolean currentItalic = false;
    private int currentFontSize = 14;

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
        gbc.gridwidth = 1;
        JPanel categoryPanel = createCategoryRadioPanel();
        headerPanel.add(categoryPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dateLabel.setForeground(ThemeService.getInstance().getSecondaryTextColor());
        headerPanel.add(dateLabel, gbc);

        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Create center panel to hold formatting toolbar and content area
        JPanel centerContentPanel = new JPanel(new BorderLayout(0, 5));
        centerContentPanel.setBackground(ThemeService.getInstance().getCardBackgroundColor());

        // Formatting toolbar
        JPanel formattingToolbar = createFormattingToolbar();
        centerContentPanel.add(formattingToolbar, BorderLayout.NORTH);

        // Content area with JTextPane for rich text
        contentArea = new JTextPane();
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        contentArea.setForeground(ThemeService.getInstance().getForegroundColor());
        contentArea.setCaretColor(ThemeService.getInstance().getForegroundColor());

        // Add document listener to apply formatting as user types
        contentArea.addCaretListener(e -> updateFormattingRadios());

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()));
        centerContentPanel.add(contentScrollPane, BorderLayout.CENTER);

        rightPanel.add(centerContentPanel, BorderLayout.CENTER);

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

    private JPanel createCategoryRadioPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(ThemeService.getInstance().getCardBackgroundColor());

        categoryGroup = new ButtonGroup();

        prayerRadio = new JRadioButton("Prayer");
        reflectionRadio = new JRadioButton("Reflection");
        intentionRadio = new JRadioButton("Intention");
        gratitudeRadio = new JRadioButton("Gratitude");
        otherRadio = new JRadioButton("Other");

        styleRadioButton(prayerRadio);
        styleRadioButton(reflectionRadio);
        styleRadioButton(intentionRadio);
        styleRadioButton(gratitudeRadio);
        styleRadioButton(otherRadio);

        categoryGroup.add(prayerRadio);
        categoryGroup.add(reflectionRadio);
        categoryGroup.add(intentionRadio);
        categoryGroup.add(gratitudeRadio);
        categoryGroup.add(otherRadio);

        panel.add(prayerRadio);
        panel.add(reflectionRadio);
        panel.add(intentionRadio);
        panel.add(gratitudeRadio);
        panel.add(otherRadio);

        prayerRadio.setSelected(true);

        return panel;
    }

    private JPanel createFormattingToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(ThemeService.getInstance().getCardBackgroundColor());
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeService.getInstance().getBorderColor()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Text Style Section
        JLabel styleLabel = new JLabel("Style:");
        styleLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        styleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        toolbar.add(styleLabel);

        styleGroup = new ButtonGroup();

        normalRadio = new JRadioButton("Normal");
        boldRadio = new JRadioButton("Bold");
        italicRadio = new JRadioButton("Italic");

        styleFormattingRadio(normalRadio);
        styleFormattingRadio(boldRadio);
        styleFormattingRadio(italicRadio);

        styleGroup.add(normalRadio);
        styleGroup.add(boldRadio);
        styleGroup.add(italicRadio);

        normalRadio.setSelected(true);

        normalRadio.addActionListener(e -> applyStyleFormatting(false, false));
        boldRadio.addActionListener(e -> applyStyleFormatting(true, false));
        italicRadio.addActionListener(e -> applyStyleFormatting(false, true));

        toolbar.add(normalRadio);
        toolbar.add(boldRadio);
        toolbar.add(italicRadio);

        // Separator
        JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setPreferredSize(new Dimension(2, 20));
        toolbar.add(separator1);

        // Font Size Section
        JLabel sizeLabel = new JLabel("Font Size:");
        sizeLabel.setForeground(ThemeService.getInstance().getForegroundColor());
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        toolbar.add(sizeLabel);

        fontSizeField = new JTextField("14", 4);
        styleFontSizeField(fontSizeField);
        toolbar.add(fontSizeField);

        applyFontSizeButton = new JButton("Apply");
        styleApplyButton(applyFontSizeButton);
        applyFontSizeButton.addActionListener(e -> applyCustomFontSize());
        toolbar.add(applyFontSizeButton);

        // Add Enter key listener to font size field
        fontSizeField.addActionListener(e -> applyCustomFontSize());

        return toolbar;
    }

    private void styleFormattingRadio(JRadioButton radioButton) {
        ThemeService theme = ThemeService.getInstance();
        radioButton.setBackground(theme.getCardBackgroundColor());
        radioButton.setForeground(theme.getForegroundColor());
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        radioButton.setFocusPainted(false);
    }

    private void styleFontSizeField(JTextField textField) {
        ThemeService theme = ThemeService.getInstance();
        textField.setBackground(theme.getCardBackgroundColor());
        textField.setForeground(theme.getForegroundColor());
        textField.setCaretColor(theme.getForegroundColor());
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor()),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        textField.setPreferredSize(new Dimension(50, 25));
    }

    private void styleApplyButton(JButton button) {
        ThemeService theme = ThemeService.getInstance();
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(theme.getAccentColor());
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setPreferredSize(new Dimension(55, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void applyCustomFontSize() {
        try {
            int size = Integer.parseInt(fontSizeField.getText().trim());

            // Validate font size range
            if (size < 8 || size > 72) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a font size between 8 and 72.",
                        "Invalid Font Size",
                        JOptionPane.WARNING_MESSAGE);
                fontSizeField.setText(String.valueOf(currentFontSize));
                return;
            }

            applyFontSize(size);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            fontSizeField.setText(String.valueOf(currentFontSize));
        }
    }

    private void applyStyleFormatting(boolean bold, boolean italic) {
        currentBold = bold;
        currentItalic = italic;

        StyledDocument doc = contentArea.getStyledDocument();
        int start = contentArea.getSelectionStart();
        int end = contentArea.getSelectionEnd();

        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setBold(sas, bold);
        StyleConstants.setItalic(sas, italic);

        if (start != end) {
            // Apply to selection
            doc.setCharacterAttributes(start, end - start, sas, false);
        }

        // Set input attributes for new typing
        contentArea.setCharacterAttributes(sas, false);
        contentArea.requestFocus();
    }

    private void applyFontSize(int size) {
        currentFontSize = size;

        StyledDocument doc = contentArea.getStyledDocument();
        int start = contentArea.getSelectionStart();
        int end = contentArea.getSelectionEnd();

        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setFontSize(sas, size);

        if (start != end) {
            // Apply to selection
            doc.setCharacterAttributes(start, end - start, sas, false);
        }

        // Set input attributes for new typing
        contentArea.setCharacterAttributes(sas, false);
        contentArea.requestFocus();
    }

    private void updateFormattingRadios() {
        // Update radio buttons based on current caret position
        StyledDocument doc = contentArea.getStyledDocument();
        int pos = contentArea.getCaretPosition();

        if (pos > 0 && pos <= doc.getLength()) {
            AttributeSet attrs = doc.getCharacterElement(pos - 1).getAttributes();

            boolean isBold = StyleConstants.isBold(attrs);
            boolean isItalic = StyleConstants.isItalic(attrs);
            int fontSize = StyleConstants.getFontSize(attrs);

            // Update style radios
            if (isBold && !isItalic) {
                boldRadio.setSelected(true);
                currentBold = true;
                currentItalic = false;
            } else if (!isBold && isItalic) {
                italicRadio.setSelected(true);
                currentBold = false;
                currentItalic = true;
            } else {
                normalRadio.setSelected(true);
                currentBold = false;
                currentItalic = false;
            }

            // Update font size field
            currentFontSize = fontSize;
            fontSizeField.setText(String.valueOf(fontSize));
        }
    }

    private void styleRadioButton(JRadioButton radioButton) {
        ThemeService theme = ThemeService.getInstance();
        radioButton.setBackground(theme.getCardBackgroundColor());
        radioButton.setForeground(theme.getForegroundColor());
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        radioButton.setFocusPainted(false);
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
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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

        // Set the appropriate radio button based on category
        String category = note.getCategory();
        switch (category) {
            case "Prayer":
                prayerRadio.setSelected(true);
                break;
            case "Reflection":
                reflectionRadio.setSelected(true);
                break;
            case "Intention":
                intentionRadio.setSelected(true);
                break;
            case "Gratitude":
                gratitudeRadio.setSelected(true);
                break;
            case "Other":
                otherRadio.setSelected(true);
                break;
            default:
                prayerRadio.setSelected(true);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        dateLabel.setText("Created: " + note.getCreatedDate().format(formatter) +
                " | Modified: " + note.getModifiedDate().format(formatter));

        setEditorEnabled(true);
    }

    private void updateCurrentNoteFromEditor() {
        if (currentNote != null) {
            currentNote.setTitle(titleField.getText().trim());
            currentNote.setContent(contentArea.getText());
            currentNote.setCategory(getSelectedCategory());
        }
    }

    private String getSelectedCategory() {
        if (prayerRadio.isSelected()) return "Prayer";
        if (reflectionRadio.isSelected()) return "Reflection";
        if (intentionRadio.isSelected()) return "Intention";
        if (gratitudeRadio.isSelected()) return "Gratitude";
        if (otherRadio.isSelected()) return "Other";
        return "Prayer"; // default
    }

    private void clearEditor() {
        titleField.setText("");
        contentArea.setText("");
        prayerRadio.setSelected(true);
        normalRadio.setSelected(true);
        fontSizeField.setText("14");
        dateLabel.setText("");
        currentNote = null;
        currentBold = false;
        currentItalic = false;
        currentFontSize = 14;
    }

    private void setEditorEnabled(boolean enabled) {
        titleField.setEnabled(enabled);
        contentArea.setEnabled(enabled);
        prayerRadio.setEnabled(enabled);
        reflectionRadio.setEnabled(enabled);
        intentionRadio.setEnabled(enabled);
        gratitudeRadio.setEnabled(enabled);
        otherRadio.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        normalRadio.setEnabled(enabled);
        boldRadio.setEnabled(enabled);
        italicRadio.setEnabled(enabled);
        fontSizeField.setEnabled(enabled);
        applyFontSizeButton.setEnabled(enabled);
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