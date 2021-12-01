package me.William278.haruhiheiretsutranslator.parser.formats;

import me.William278.haruhiheiretsutranslator.parser.DataFile;
import me.William278.haruhiheiretsutranslator.parser.utils.DataFileReader;
import me.William278.haruhiheiretsutranslator.searcher.Search;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScriptFile extends DataFile {

    public ArrayList<DataItem> data;

    public ScriptFile(byte[] rawData, String fileName, String filePath, String parentFileName, FileType format) {
        super(rawData, fileName, filePath, parentFileName, format);
        data = new ArrayList<>();
    }

    public ScriptFile newFromThis() {
        ScriptFile file = new ScriptFile(getByteArray(), fileName, filePath, parentFileName, format);
        file.data = new ArrayList<>(data);
        return file;
    }

    public void addDataItem(DataItem dataItem) {
        data.add(dataItem);
    }

    @Override
    public byte[] getByteArray() {
        ArrayList<Byte> bytes = new ArrayList<>();
        for (DataItem d : data) {
            for (byte b : d.data) {
                bytes.add(b);
            }
        }
        return DataFileReader.listToByteArray(bytes);
    }

    public void updateDataSequence(DataItem sequence) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSequence) {
                if (data.get(i).uuid.equals(sequence.uuid)) {
                    data.set(i, sequence);
                }
            }
        }
    }



    @Override
    public ArrayList<Search.SearchResult> searchFile(String searchTerm) {
        ArrayList<Search.SearchResult> results = new ArrayList<>();
        int lineNumber = 0;
        for (ScriptFile.DataItem item : data.stream().filter(dataItem -> dataItem.isSequence).toList()) {
            if (item.toShiftJis().contains(searchTerm)) {
                results.add(new Search.SearchResult(filePath, parentFileName, fileName, "line " + lineNumber, format));
            }
            lineNumber++;
        }
        return results;
    }

    // Get a DataItem from a byte array and assign it a flag if it is a text / information sequence
    public static ScriptFile.DataItem getDataItem(List<Byte> currentSequence, boolean isSequence) {
        // Convert sequence ArrayList to byte array
        byte[] data = DataFileReader.listToByteArray(currentSequence);

        return new ScriptFile.DataItem(data, isSequence);
    }

    public static class DataItem {

        public boolean isSequence;

        public UUID uuid;

        public byte[] data;

        public DataItem(byte[] data, boolean isSequence) {
            this.data = data;
            this.isSequence = isSequence;
            this.uuid = UUID.randomUUID();
        }

        public DataItem(byte[] data, boolean isSequence, UUID uuid) {
            this.data = data;
            this.isSequence = isSequence;
            this.uuid = uuid;
        }

        public static byte[] toByteArray(String text) {
            byte[] bytes = text.getBytes(Charset.forName("SHIFT_JIS"));
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == (byte) 0x2B) {
                    bytes[i] = 0x0A;
                }
            }
            return bytes;
        }

        public String toShiftJis() {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == (byte) 0x0A) {
                    data[i] = (byte) 0x2B;
                }
            }
            return new String(data, Charset.forName("SHIFT_JIS"));
        }

    }
}
