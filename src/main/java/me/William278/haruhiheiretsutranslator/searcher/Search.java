package me.William278.haruhiheiretsutranslator.searcher;

import me.William278.haruhiheiretsutranslator.parser.DataFile;
import me.William278.haruhiheiretsutranslator.parser.formats.ScriptFile;
import me.William278.haruhiheiretsutranslator.parser.utils.DataFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Search implements Runnable {

    private final File directory;
    private final String searchTerm;
    private final boolean showBinaryFiles;

    public Search(String directoryPath, String searchTerm, boolean showBinaryFiles) {
        this.directory = new File(directoryPath);
        this.searchTerm = searchTerm;
        this.showBinaryFiles = showBinaryFiles;
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
            DataFile readFile = DataFileReader.readFile(file.getPath());
            if (readFile == null) {
                continue;
            }

            // Don't include binary files if the flag is set
           if (!(readFile instanceof ScriptFile)) {
                if (!showBinaryFiles) {
                    continue;
                }
            }

            // Search the file
            results.addAll(readFile.searchFile(searchTerm));

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

    public record SearchResult(String filePath, String parentFolderName, String fileName, String locationInFile, DataFile.FileType fileType) {
        public String[] getFormattedResult() {
            return new String[] {parentFolderName, fileName, fileType.label, locationInFile};
        }
    }

}
