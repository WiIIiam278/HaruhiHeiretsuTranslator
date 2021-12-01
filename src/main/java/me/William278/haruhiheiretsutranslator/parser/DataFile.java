package me.William278.haruhiheiretsutranslator.parser;

import me.William278.haruhiheiretsutranslator.parser.formats.ScriptFile;
import me.William278.haruhiheiretsutranslator.searcher.Search;

import java.util.ArrayList;

public class DataFile {

    public String fileName;
    public String filePath;
    public String parentFileName;
    private final byte[] rawData;
    public FileType format;

    public DataFile(byte[] rawData, String fileName, String filePath, String parentName, FileType format) {
        this.rawData = rawData;
        this.fileName = fileName;
        this.filePath = filePath;
        this.parentFileName = parentName;
        this.format = format;
    }

    public ArrayList<Search.SearchResult> searchFile(String searchTerm) {
        ArrayList<Search.SearchResult> results = new ArrayList<>();
        byte[] searchTermBytes = ScriptFile.DataItem.toByteArray(searchTerm);
        int matchedBytes = 0;
        for (int i = 0; i < rawData.length; i++) {
            byte currentByte = rawData[i];
            if (currentByte == searchTermBytes[matchedBytes]) {
                matchedBytes++;
                if (matchedBytes == searchTermBytes.length) {
                    results.add(new Search.SearchResult(filePath, parentFileName, fileName, "byte " + Integer.toHexString(i), format));
                    matchedBytes = 0;
                }
            } else {
                matchedBytes = 0;
            }
        }
        return results;
    }

    public byte[] getByteArray() {
        return rawData;
    }

    public enum FileType {
        BINARY("Binary"),
        DIALOGUE_SCRIPT("Dialogue Script"),
        SYSTEM_TEXT_SCRIPT_FILE("System Text Script");

        public final String label;

        FileType(String label) {
            this.label = label;
        }
    }

}
