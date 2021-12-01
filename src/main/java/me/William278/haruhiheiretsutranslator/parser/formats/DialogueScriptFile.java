package me.William278.haruhiheiretsutranslator.parser.formats;

import java.util.ArrayList;
import java.util.List;

public class DialogueScriptFile extends ScriptFile {

    public DialogueScriptFile(byte[] rawData, String fileName, String filePath, String parentFileName) {
        super(rawData, fileName, filePath, parentFileName, FileType.DIALOGUE_SCRIPT);
    }

    public static DialogueScriptFile readFile(byte[] fileBytes, String fileName, String filePath, String fileParentName) {
        // Create a new ScriptFile to return
        DialogueScriptFile scriptFile = new DialogueScriptFile(fileBytes, fileName, filePath, fileParentName);

        // Return null if the file could not be read
        if (fileBytes.length == 0) {
            return null;
        }

        // Initialize byte iteration flags
        short consecutiveZeroes = 0; // The number of consecutive 0x00 (NULL) bytes read
        ArrayList<Byte> currentSequence = new ArrayList<>(); // The current sequence
        byte sequenceSize = 0x00; // Byte representing the size of the sequence

        // Iterate through every byte
        for (byte currentByte : fileBytes) {
            if (currentByte == 0x00) {
                consecutiveZeroes++;
            } else {
                consecutiveZeroes = 0;
            }

            // Set the current byte to equal the sequence size
            if (currentSequence.isEmpty()) {
                sequenceSize = currentByte;
            }

            // Add the current byte to the current sequence
            currentSequence.add(currentByte);

            if (consecutiveZeroes == 4) {
                final List<Byte> sequenceToAdd = currentSequence.subList(0, currentSequence.size() - 4);
                final List<Byte> zeroesToAdd = currentSequence.subList(currentSequence.size() - 4, currentSequence.size());

                // If the sequence size matches the byte count of sequence to add, add it
                if (sequenceToAdd.size() == sequenceSize && sequenceSize > 2) {
                    final List<Byte> sizeByteToAdd = sequenceToAdd.subList(0, 1);
                    final List<Byte> dataBytesToAdd = sequenceToAdd.subList(1, sequenceToAdd.size());

                    scriptFile.addDataItem(getDataItem(sizeByteToAdd, false));
                    scriptFile.addDataItem(getDataItem(dataBytesToAdd, true));
                } else {
                    scriptFile.addDataItem(getDataItem(sequenceToAdd, false));
                }
                scriptFile.addDataItem(getDataItem(zeroesToAdd, false));

                // Reset flags
                consecutiveZeroes = 0;
                currentSequence = new ArrayList<>();
                sequenceSize = 0x00;
            }
        }
        // Add leftover data at end of file byte array
        if (!currentSequence.isEmpty()) {
            scriptFile.addDataItem(getDataItem(currentSequence, false));
        }

        // Return the script file
        return scriptFile;
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
