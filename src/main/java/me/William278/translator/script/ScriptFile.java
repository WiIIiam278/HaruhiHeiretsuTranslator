package me.William278.translator.script;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class ScriptFile {

    public ArrayList<DataItem> data;

    public ScriptFile() {
        data = new ArrayList<>();
    }

    // Constructor for a new ScriptFile object based on an existing file
    public ScriptFile(ScriptFile other) {
        data = new ArrayList<>();
        data.addAll(other.data);
    }

    public void addDataItem(DataItem dataItem) {
        data.add(dataItem);
    }

    public byte[] toByteArray() {
        ArrayList<Byte> bytes = new ArrayList<>();
        for (DataItem d : data) {
            for (byte b : d.data) {
                bytes.add(b);
            }
        }
        return ScriptFileReader.toByteArray(bytes);
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

    // Returns a character name from a DataSequence containing a voiced line
    public static String getCharacterName(DataItem voiceLineIdSequence) {
        return switch (getCharacterId(voiceLineIdSequence).toLowerCase()) {
            case "hrh" -> "Haruhi Suzumiya";
            case "kyn" -> "Kyon";
            case "mnl" -> "Kyon (Monologue)";
            case "sis" -> "Kyon's Sister";
            case "try" -> "Tsuruya-san";
            case "ngt" -> "Yuki Nagato";
            case "kzm" -> "Itsuki Koizumi";
            case "mkr" -> "Mikuru Asahina";
            case "tan" -> "Taniguchi";
            case "kun" -> "Kunikida";
            case "mkt" -> "Mikoto";
            case "tai" -> "Taiichirou";
            case "cap" -> "The Captain";
            case "ann" -> "Announcer";
            default -> getCharacterId(voiceLineIdSequence);
        };
    }

    // Returns a character ID from a DataSequence containing a voiced line
    public static String getCharacterId(DataItem voiceLineIdSequence) {
        final String voiceLineId = voiceLineIdSequence.toShiftJis();
        return voiceLineId.substring(voiceLineId.length() - 2);
    }
}
