package me.William278.haruhiheiretsutranslator.searcher;

import me.William278.haruhiheiretsutranslator.gui.TextSearcher;
import me.William278.haruhiheiretsutranslator.parser.ScriptFile;
import me.William278.haruhiheiretsutranslator.parser.ScriptFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Search implements Runnable {

    private final File directory;
    private final String searchTerm;

    public Search(String directoryPath, String searchTerm) {
        this.directory = new File(directoryPath);
        this.searchTerm = searchTerm;
    }

    private ArrayList<SearchResult> search() {
        if (!directory.exists()) {
            return null;
        }
        if (!directory.isDirectory()) {
            return null;
        }
        if (searchTerm.isEmpty()) {
            return null;
        }
        File[] subFiles = directory.listFiles();
        assert subFiles != null;

        // Get sorted list of files to work with
        List<File> files = getAllFiles(subFiles).stream().filter(file -> (file.getName().endsWith(".bin"))).toList();

        // Iterate through and search through files
        ArrayList<SearchResult> results = new ArrayList<>();
        fileCount = files.size();
        for (File file : files) {
            // Load each file into a Script
            ScriptFile fileAsScript = ScriptFileReader.readFile(file.getPath());
            if (fileAsScript == null) {
                continue;
            }

            int lineNumber = 0;
            for (ScriptFile.DataItem item : fileAsScript.data.stream().filter(dataItem -> dataItem.isSequence).toList()) {
                if (item.toShiftJis().contains(searchTerm)) {
                    results.add(new SearchResult(file.getPath(), file.getParentFile().getName(), file.getName(), lineNumber));
                }
                lineNumber++;
            }
            filesSearched++;
        }
        return results;
    }

    private ArrayList<File> getAllFiles(File[] files) {
        ArrayList<File> fileCollection = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                final File[] subFiles = file.listFiles();
                assert subFiles != null;
                fileCollection.addAll(getAllFiles(subFiles));
            } else {
                fileCollection.add(file);
            }
        }
        return fileCollection;
    }

    public ArrayList<SearchResult> results;
    public boolean isSearchDone = false;

    public int fileCount;
    public int filesSearched;

    @Override
    public void run() {
        results = search();
        isSearchDone = true;
    }

    public record SearchResult(String filePath, String parentFolderName, String fileName, int lineNumber) {
        public String getFormattedResult() {
            return parentFolderName + "/" + fileName + " (" + lineNumber + ")";
        }
    }

}
