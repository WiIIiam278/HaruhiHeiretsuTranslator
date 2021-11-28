package me.William278.gui;

import me.William278.translator.script.ScriptFile;
import me.William278.translator.script.ScriptFileReader;
import me.William278.translator.script.ScriptFileSaver;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public class TranslatorApp {
    private JTextArea FileEditorData;
    private JPanel MainPanel;
    private JTextField SelectedFilePath;
    private JButton BrowseButton;
    private JScrollPane FileEditorScrollPane;
    private JButton SaveButton;
    private JLabel EditingTooltip;

    public static ScriptFile loadedFile;
    public static File workingDirectory = new File(System.getProperty("user.home"));

    public TranslatorApp() {
        // When a user presses the Browse button
        BrowseButton.addActionListener(e -> onBrowseButtonPressed());

        // Set default text of text box
        FileEditorData.setText("Select a file to edit...\n\n\n\n\n\n\n\n");

        // When a user moves the caret
        FileEditorData.addCaretListener(e -> updateEditingTooltip());

        // When a user presses the Save As button
        SaveButton.addActionListener(e -> onSaveAsButtonPressed());
    }

    private String getRowText(int row) {
        String[] textArray = FileEditorData.getText().split("\n");
        ArrayList<String> rowText = new ArrayList<>(Arrays.asList(textArray));
        if (row < 0 || row >= rowText.size()) {
            return null;
        }
        return rowText.get(row);
    }

    private void updateEditingTooltip() {
        if (!FileEditorData.isEditable()) {
            return;
        }
        try {
            Caret caret = FileEditorData.getCaret();
            if (caret != null) {
                int caretPosition = FileEditorData.getCaretPosition();
                int row = FileEditorData.getLineOfOffset(caretPosition);
                if (row >= 0 && row < loadedFile.data.size()) {
                    ScriptFile.DataItem sequence = loadedFile.data.stream().filter(dataItem -> dataItem.isSequence).toList().get(row);
                    String currentRowText = getRowText(row);
                    if (sequence != null && currentRowText != null) {
                        byte[] currentRowBytes = currentRowText.getBytes(Charset.forName("SHIFT_JIS"));
                        EditingTooltip.setText("Editing Line: " + row + " (" + currentRowBytes.length + "/" + sequence.data.length + ")");
                        return;
                    }
                }
                EditingTooltip.setText("Editing Line: " + row);
            }
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(MainPanel, "Error: BadLocationException",
                    "An exception occurred", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static class BinFileFilter extends FileFilter {
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
    }

    private void onSaveAsButtonPressed() {
        try {
            File currentDir = workingDirectory;
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(currentDir);
            fileSaver.addChoosableFileFilter(new BinFileFilter());
            fileSaver.setAcceptAllFileFilterUsed(true);
            fileSaver.setSelectedFile(new File(currentDir.getPath() + File.separator + "00000003.bin"));
            int result = fileSaver.showSaveDialog(MainPanel);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String selectedPath = fileSaver.getSelectedFile() != null ? fileSaver.getSelectedFile().getCanonicalPath() : "";
            if (selectedPath.isEmpty()) {
                return;
            }
            saveFile(selectedPath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainPanel, "Please select where you want to save the bin",
                    "Error saving file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBrowseButtonPressed() {
        JFileChooser fileBrowser = new JFileChooser();
        fileBrowser.setCurrentDirectory(workingDirectory);
        fileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileBrowser.addChoosableFileFilter(new BinFileFilter());
        fileBrowser.setAcceptAllFileFilterUsed(true);
        int option = fileBrowser.showOpenDialog(MainPanel);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String selectedPath = fileBrowser.getSelectedFile() != null ? fileBrowser.getSelectedFile().getPath() : "";
        workingDirectory = new File(selectedPath).getParentFile();
        SelectedFilePath.setText(selectedPath);
        if (selectedPath.isEmpty()) {
            return;
        }
        readFile();
    }

    private void saveFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            JOptionPane.showMessageDialog(MainPanel, "You cannot overwrite an existing file!",
                    "Error saving file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int rows = FileEditorData.getText().split("\n").length;

        // Save a new ScriptFile with changes from source
        ScriptFile newFile = new ScriptFile(loadedFile);
        for (int row = 0; row < rows; row++) {
            ScriptFile.DataItem sequenceBeingEdited = loadedFile.data.stream().filter(data -> (data.isSequence)).toList().get(row);
            String currentRowText = getRowText(row);
            if (sequenceBeingEdited != null && currentRowText != null) {
                byte[] currentRowBytes = ScriptFile.DataItem.toByteArray(currentRowText);

                // Verify the byte length; if it's too short, append with 0x20, if it's too long, abort with error
                byte[] verifiedBytes = new byte[sequenceBeingEdited.data.length];
                System.arraycopy(currentRowBytes, 0, verifiedBytes, 0, verifiedBytes.length);
                if (currentRowBytes.length > sequenceBeingEdited.data.length) {
                    // Determine the difference and show error to user
                    final int lengthDifference = currentRowBytes.length - sequenceBeingEdited.data.length;
                    final String pluralByteIndicator = lengthDifference == 1 ? "" : "s"; // Show "byte" instead of "bytes" if only one byte is affected
                    JOptionPane.showMessageDialog(MainPanel, "Failed to save file:\nError on line #" + row + "; " + lengthDifference + " byte" + pluralByteIndicator + " too long.",
                            "Error saving file", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (currentRowBytes.length < sequenceBeingEdited.data.length) {
                    // Add 0x20 (SPACE) until it's the needed length
                    for (int i = currentRowBytes.length-1; i < verifiedBytes.length; i++) {
                        verifiedBytes[i] = 0x20;
                    }
                }

                // Update the DataItem with the new one
                newFile.updateDataSequence(new ScriptFile.DataItem(verifiedBytes, true, sequenceBeingEdited.uuid));
            }
        }
        ScriptFileSaver.saveScript(newFile, filePath);
        JOptionPane.showMessageDialog(MainPanel, "Saved to " + filePath,
                "File saved successfully", JOptionPane.INFORMATION_MESSAGE);
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
        for (ScriptFile.DataItem dataItem : file.data.stream().filter(dataItem -> dataItem.isSequence).toList()) {
            textToSet.add(dataItem.toShiftJis());
        }

        // Update the text
        FileEditorData.setText(textToSet.toString());

        // Enable options
        FileEditorData.setEditable(true);
        SaveButton.setEnabled(true);

        // Set the vertical scrollbar and ticking caret positions to 0
        SwingUtilities.invokeLater(() -> {
            FileEditorData.setCaretPosition(0);
            FileEditorScrollPane.getVerticalScrollBar().setValue(0);
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
