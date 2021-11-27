package me.William278.translator.script;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ScriptFile {
    public ArrayList<DataSequence> lines;

    public ScriptFile() {
        lines = new ArrayList<>();
    }

    public void addLanguageLine(DataSequence line) {
        lines.add(line);
    }

    public record DataSequence(byte[] textData, int sequenceStartPosition) {
        public String toShiftJis() {
            return new String(textData, Charset.forName("SHIFT_JIS"));
        }

        public String toUtf8() {
            return new String(textData, StandardCharsets.UTF_8);
        }
    }

    // Returns a character name from a DataSequence containing a voiced line
    public static String getCharacterName(DataSequence voiceLineIdSequence) {
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
    public static String getCharacterId(DataSequence voiceLineIdSequence) {
        final String voiceLineId = voiceLineIdSequence.toShiftJis();
        return voiceLineId.substring(voiceLineId.length()-2);
    }
}
