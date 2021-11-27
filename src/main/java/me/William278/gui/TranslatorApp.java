package me.William278.gui;

import me.William278.translator.script.ScriptFile;
import me.William278.translator.script.ScriptFileReader;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public class TranslatorApp {
    private JTextArea FilePreviewData;
    private JPanel MainPanel;
    private JTextField SelectedFilePath;
    private JButton ReadFileButton;
    private JButton BrowseButton;
    private JScrollPane FilePreviewScrollPane;
    private JButton SaveButton;
    private JLabel EditingTooltip;

    public static ScriptFile loadedFile;

    public TranslatorApp() {
        // When a user presses the Browse button
        BrowseButton.addActionListener(e -> onBrowseButtonPressed());

        // Set default text of text box
        FilePreviewData.setText("Select a 3.bin file to load, then select \"Read File\"\n\n\n\n\n\n\n\n");

        // When a user moves the caret
        FilePreviewData.addCaretListener(new CaretListener() {
            /**
             * Called when the caret position is updated.
             *
             * @param e the caret event
             */
            @Override
            public void caretUpdate(CaretEvent e) {
                updateEditingTooltip();
            }
        });
    }

    private String getRowText(int row) {
        String[] textArray = FilePreviewData.getText().split("\n");
        ArrayList<String> rowText = new ArrayList<>(Arrays.asList(textArray));
        if (row < 0 || row >= rowText.size()) {
            return null;
        }
        return rowText.get(row);
    }

    private void updateEditingTooltip() {
        try {
            Caret caret = FilePreviewData.getCaret();
            if (caret != null) {
                int caretPosition = FilePreviewData.getCaretPosition();
                int row = FilePreviewData.getLineOfOffset(caretPosition);
                if (row >= 0 && row < loadedFile.lines.size()) {
                    ScriptFile.DataSequence sequence = loadedFile.lines.get(row);
                    String currentRowText = getRowText(row);
                    if (sequence != null && currentRowText != null) {
                        byte[] currentRowBytes = currentRowText.getBytes(Charset.forName("SHIFT_JIS"));
                        EditingTooltip.setText("Editing: " + row + " (" + currentRowBytes.length + "/" + sequence.textData().length + ")");
                        return;
                    }
                }
                EditingTooltip.setText("Editing: " + row);
            }
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(MainPanel, "Error: BadLocationException",
                    "An exception occurred", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onBrowseButtonPressed() {
        JFileChooser j = new JFileChooser();
        j.setCurrentDirectory(new File(System.getProperty("user.home")));
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);
        j.addChoosableFileFilter(new FileFilter() {
            public String getDescription() {
                return "Suzumiya Haruhi no Heiretsu Text Binaries (*.bin)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".bin");
                }
            }
        });
        j.setAcceptAllFileFilterUsed(true);
        j.showOpenDialog(MainPanel);
        String selectedPath = j.getSelectedFile() != null ? j.getSelectedFile().getPath() : "";
        SelectedFilePath.setText(selectedPath);
        readFile();
    }

    private void readFile() {
        if (SelectedFilePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(MainPanel, "Please select a text bin file first!",
                    "Error reading file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ScriptFile readScript = ScriptFileReader.readFile(SelectedFilePath.getText());
        if (readScript == null) {
            JOptionPane.showMessageDialog(MainPanel, "Could not read that file",
                    "Error reading file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadedFile = readScript;
        setTextFromScriptFile();
    }

    private void setTextFromScriptFile() {
        ScriptFile file = loadedFile;
        StringJoiner textToSet = new StringJoiner("\n");
        for (ScriptFile.DataSequence line : file.lines) {
            textToSet.add(line.toShiftJis());
        }

        // Update the text
        FilePreviewData.setText(textToSet.toString());

        // Set the vertical scrollbar and ticking caret positions to 0
        SwingUtilities.invokeLater(() -> {
            FilePreviewData.setCaretPosition(0);
            FilePreviewScrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    public static void main(String[] args) {
        URL iconUrl = TranslatorApp.class.getClassLoader().getResource("AppIcon.png");
        assert iconUrl != null;
        setUITheme();
        JFrame frame = new JFrame("Haruhi Heiretsu Translator");
        frame.setContentPane(new TranslatorApp().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(iconUrl.getPath()).getImage());
        frame.pack();
        frame.setVisible(true);
    }

    private static void setUITheme() {
        try {
            // Set interface look and feel
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
