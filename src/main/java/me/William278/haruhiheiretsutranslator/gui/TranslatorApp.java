package me.William278.haruhiheiretsutranslator.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.William278.haruhiheiretsutranslator.parser.DataFile;
import me.William278.haruhiheiretsutranslator.parser.formats.ScriptFile;
import me.William278.haruhiheiretsutranslator.parser.utils.DataFileReader;
import me.William278.haruhiheiretsutranslator.parser.utils.DataFileSaver;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class TranslatorApp extends JFrame {
    private JTextArea FileEditorData;
    private JPanel MainPanel;
    public JTextField SelectedFilePath;
    private JButton BrowseButton;
    private JScrollPane FileEditorScrollPane;
    private JButton SaveButton;
    private JLabel EditingTooltip;
    private JButton SearchButton;

    public static ScriptFile loadedFile;
    public static File workingDirectory = new File(System.getProperty("user.home"));

    private static TranslatorApp instance;
    public static TranslatorApp getInstance() {
        return instance;
    }

    public TranslatorApp() {
        instance = this;

        // Initialize form
        setTitle("Haruhi Heiretsu Translator");
        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 500));
        pack();
        setVisible(true);
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("AppIcon.png")));
        } catch (Exception e) {
            System.out.println("An exception occurred loading the icon image.");
        }

        // When a user presses the Browse button
        BrowseButton.addActionListener(e -> onBrowseButtonPressed());

        // Set default text of text box
        FileEditorData.setText("Select a file to edit...");

        // When a user moves the caret
        FileEditorData.addCaretListener(e -> updateEditingTooltip());

        // When a user presses the Save As button
        SaveButton.addActionListener(e -> onSaveAsButtonPressed());

        // When a user presses the Search button
        SearchButton.addActionListener(e -> onSearchButtonPressed());
    }

    private void onSearchButtonPressed() {
        if (TextSearcher.getInstance() == null) {
            new TextSearcher();
        } else {
            TextSearcher textSearcher = TextSearcher.getInstance();
            textSearcher.setVisible(true);
            textSearcher.toFront();
        }
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
                final List<ScriptFile.DataItem> filteredItems = loadedFile.data.stream().filter(dataItem -> dataItem.isSequence).toList();
                if (row >= 0 && row < filteredItems.size()) {
                    ScriptFile.DataItem sequence = filteredItems.get(row);
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        MainPanel = new JPanel();
        MainPanel.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        SelectedFilePath = new JTextField();
        SelectedFilePath.setEditable(false);
        SelectedFilePath.setEnabled(true);
        MainPanel.add(SelectedFilePath, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(429, 30), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("File Editor");
        MainPanel.add(label1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(429, 16), null, 0, false));
        BrowseButton = new JButton();
        BrowseButton.setIcon(new ImageIcon(getClass().getResource("/OpenIcon.png")));
        BrowseButton.setText("Open...");
        MainPanel.add(BrowseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FileEditorScrollPane = new JScrollPane();
        MainPanel.add(FileEditorScrollPane, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        FileEditorData = new JTextArea();
        FileEditorData.setEditable(false);
        FileEditorData.setEnabled(true);
        FileEditorData.setLineWrap(false);
        FileEditorData.setText("");
        FileEditorScrollPane.setViewportView(FileEditorData);
        final JLabel label2 = new JLabel();
        label2.setText("Select File");
        MainPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(429, 16), null, 0, false));
        EditingTooltip = new JLabel();
        EditingTooltip.setText("Editing Line: N/A");
        MainPanel.add(EditingTooltip, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SaveButton = new JButton();
        SaveButton.setEnabled(false);
        SaveButton.setIcon(new ImageIcon(getClass().getResource("/SaveIcon.png")));
        SaveButton.setText("Save As...");
        MainPanel.add(SaveButton, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SearchButton = new JButton();
        SearchButton.setIcon(new ImageIcon(getClass().getResource("/SearchIcon.png")));
        SearchButton.setText("Search...");
        MainPanel.add(SearchButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

    public static class BinFileFilter extends FileFilter {
        public String getDescription() {
            return "Suzumiya Haruhi no Heiretsu Script Binaries (*.bin)";
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
            fileSaver.setSelectedFile(new File(currentDir.getPath() + File.separator + loadedFile.fileName + "-out.bin"));
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
        // Ensure that the user is not overwriting an existing file
        File file = new File(filePath);
        if (file.exists()) {
            JOptionPane.showMessageDialog(MainPanel, "You cannot overwrite an existing file!",
                    "Error saving file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the number of rows
        int rows = FileEditorData.getText().split("\n").length;

        // Save a new ScriptFile with changes from source
        ScriptFile newFile = loadedFile.newFromThis();
        for (int row = 0; row < rows; row++) {
            ScriptFile.DataItem sequenceBeingEdited = loadedFile.data.stream().filter(data -> (data.isSequence)).toList().get(row);
            String currentRowText = getRowText(row);
            if (sequenceBeingEdited != null && currentRowText != null) {
                final byte[] thisRowBytes = ScriptFile.DataItem.toByteArray(currentRowText);

                // Verify the byte length, if it's too long, abort with error
                if (thisRowBytes.length > sequenceBeingEdited.data.length) {
                    // Determine the difference and show error to user
                    final int lengthDifference = thisRowBytes.length - sequenceBeingEdited.data.length;
                    final String pluralByteIndicator = lengthDifference == 1 ? "" : "s"; // Show "byte" instead of "bytes" if only one byte is affected
                    JOptionPane.showMessageDialog(MainPanel, "Failed to save file:\nError on line #" + row + "; " + lengthDifference + " byte" + pluralByteIndicator + " too long.",
                            "Error saving file", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Ensure the output bytes matches the correct sequence length
                final ArrayList<Byte> finalBytes = new ArrayList<>();
                for (int i = 0; i < sequenceBeingEdited.data.length; i++) {
                    finalBytes.add((byte) 0x20);
                }

                // Set the bytes correctly
                for (int i = 0; (i < finalBytes.size() && i < thisRowBytes.length); i++) {
                    byte byteToAdd = thisRowBytes[i];
                    finalBytes.set(i, byteToAdd);
                }

                // Update the DataItem with the new one
                newFile.updateDataSequence(new ScriptFile.DataItem(DataFileReader.listToByteArray(finalBytes), true, sequenceBeingEdited.uuid));
            }
        }
        DataFileSaver.saveScript(newFile, filePath);
        JOptionPane.showMessageDialog(MainPanel, "Saved to " + filePath,
                "File saved successfully", JOptionPane.INFORMATION_MESSAGE);
    }

    public void readFile() {
        if (SelectedFilePath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(MainPanel, "Please select a text bin file first!",
                    "Error reading file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DataFile file = DataFileReader.readFile(SelectedFilePath.getText());
        if (file == null) {
            JOptionPane.showMessageDialog(MainPanel, "Could not read that file",
                    "Error reading file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (file.format != DataFile.FileType.BINARY) {
            // Initialize the loaded file
            loadedFile = (ScriptFile) file;

            // Set the text editor to the file script
            setTextFromScriptFile();
        } else {
            JOptionPane.showMessageDialog(MainPanel, "That file is not a valid script file",
                    "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
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
        setUITheme();
        TranslatorApp application = new TranslatorApp();
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
