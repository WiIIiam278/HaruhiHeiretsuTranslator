package me.William278.haruhiheiretsutranslator.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.William278.haruhiheiretsutranslator.searcher.Search;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static me.William278.haruhiheiretsutranslator.gui.TranslatorApp.workingDirectory;

public class TextSearcher extends JFrame {
    private JTextField FileSelectField;
    private JButton SelectFileButton;
    private JTextField SearchTermField;
    private JButton SearchButton;
    private JList SearchResults;
    private JScrollPane SearchResultsScroller;
    private JButton OpenSelectedButton;
    private JPanel SearchPanel;
    private JProgressBar SearchProgress;

    private static TextSearcher instance;
    public static TextSearcher getInstance() {
        return instance;
    }

    public TextSearcher() {
        instance = this;

        // Initialize form
        setTitle("Haruhi Heiretsu Translator - Search");
        setContentPane(SearchPanel);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setMinimumSize(new Dimension(400, 300));
        pack();
        toFront();
        setVisible(true);
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("AppIcon.png")));
        } catch (Exception e) {
            System.out.println("An exception occurred loading the icon image.");
        }

        SelectFileButton.addActionListener(e -> onSelectFileButtonPressed());

        SearchButton.addActionListener(e -> onSearchButtonPressed());

        OpenSelectedButton.addActionListener(e -> {
            onOpenSelectedButtonPressed();
        });
    }

    private void onSelectFileButtonPressed() {
        JFileChooser fileBrowser = new JFileChooser();
        fileBrowser.setCurrentDirectory(workingDirectory);
        fileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileBrowser.showOpenDialog(SearchPanel);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String selectedPath = fileBrowser.getSelectedFile() != null ? fileBrowser.getSelectedFile().getPath() : "";
        final File selectedFile = new File(selectedPath);
        workingDirectory = selectedFile.getParentFile();
        FileSelectField.setText(selectedPath);
    }

    private void onSearchButtonPressed() {
        String filePath = FileSelectField.getText();

        String searchTerm = SearchTermField.getText();

        SearchProgress.setValue(0);
        SearchButton.setEnabled(false);
        Search search = new Search(filePath, searchTerm);
        Thread searchThread = new Thread("Test") {
            @Override
            public void run() {
                search.run();
                while (!search.isSearchDone) {
                    SearchProgress.setValue((search.filesSearched / search.fileCount) * 100);
                }
                ArrayList<Search.SearchResult> results = search.results;
                if (results == null) {
                    JOptionPane.showMessageDialog(SearchPanel, "Invalid search parameters.",
                            "Error while searching", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                updateSearchResults(results);
                SearchButton.setEnabled(true);
                SearchProgress.setValue(100);
            }
        };
        searchThread.start();
    }

    private ArrayList<Search.SearchResult> completedResults;
    private void updateSearchResults(ArrayList<Search.SearchResult> results) {
        completedResults = new ArrayList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Search.SearchResult result : results) {
            completedResults.add(result);
            model.addElement(result.getFormattedResult());
        }
        SearchResults.setModel(model);
    }

    private void onOpenSelectedButtonPressed() {
        Search.SearchResult result = getSelectedResult();
        if (result == null) {
            JOptionPane.showMessageDialog(SearchPanel, "Please select a result to open",
                    "Error while opening", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TranslatorApp translatorApp = TranslatorApp.getInstance();
        translatorApp.SelectedFilePath.setText(result.filePath());
        if (result.filePath().isEmpty()) {
            JOptionPane.showMessageDialog(SearchPanel, "Please select a result to open (invalid path)",
                    "Error while opening", JOptionPane.ERROR_MESSAGE);
            return;
        }
        translatorApp.readFile();
        toBack();
        setVisible(false);
        translatorApp.toFront();
    }

    private Search.SearchResult getSelectedResult() {
        if (SearchResults.isSelectionEmpty()) {
            return null;
        }
        int selectedResult = SearchResults.getSelectedIndex();
        return completedResults.get(selectedResult);
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
        SearchPanel = new JPanel();
        SearchPanel.setLayout(new GridLayoutManager(7, 4, new Insets(0, 0, 0, 0), -1, -1));
        SearchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Search for text in bin files");
        SearchPanel.add(label1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FileSelectField = new JTextField();
        FileSelectField.setEditable(false);
        SearchPanel.add(FileSelectField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Select folder");
        SearchPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SearchTermField = new JTextField();
        SearchPanel.add(SearchTermField, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Search term");
        SearchPanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SearchResultsScroller = new JScrollPane();
        SearchPanel.add(SearchResultsScroller, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        SearchResults = new JList();
        SearchResults.setEnabled(true);
        SearchResultsScroller.setViewportView(SearchResults);
        final JLabel label4 = new JLabel();
        label4.setText("Search results");
        SearchPanel.add(label4, new GridConstraints(4, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OpenSelectedButton = new JButton();
        OpenSelectedButton.setIcon(new ImageIcon(getClass().getResource("/OpenIcon.png")));
        OpenSelectedButton.setText("Edit Selected");
        SearchPanel.add(OpenSelectedButton, new GridConstraints(6, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SearchButton = new JButton();
        SearchButton.setIcon(new ImageIcon(getClass().getResource("/SearchIcon.png")));
        SearchButton.setText("Search");
        SearchPanel.add(SearchButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SelectFileButton = new JButton();
        SelectFileButton.setIcon(new ImageIcon(getClass().getResource("/OpenIcon.png")));
        SelectFileButton.setText("Open...");
        SearchPanel.add(SelectFileButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SearchProgress = new JProgressBar();
        SearchPanel.add(SearchProgress, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return SearchPanel;
    }

}
