package me.William278.translator.script;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

/*
 * This class reads in script files for Suzumiya Haruhi no Heiretsu
 * Script files are ones that contain in-game dialogue, located in 3.bin in .BLN Subs 75-297 within the MCB filesystem
 */
public class ScriptFileReader {

    public static ScriptFile readFile(String filePath) {
        // Get the file at the path; return null if it does not exist
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        ScriptFile scriptFile = new ScriptFile();
        try {
            // Load the file as a byte array
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Iterate through every byte in the array to find "sequences" (text, control codes and voice line identifiers)
            int zeroBytes = 0; // Number of consecutive NULL (0x00 bytes) read
            int sequenceStartIndex = 0; // Index number in byte array where the sequence starts
            short sequenceLength = 0; // Length of the sequence, indicated by the first byte
            ArrayList<Byte> sequenceArray = new ArrayList<>(); // Array of the last bytes that have been read

            for (int fileIndex = 0; fileIndex < fileBytes.length; fileIndex++) {
                if (sequenceArray.isEmpty()) {
                    sequenceStartIndex = fileIndex;
                }
                byte currentByte = fileBytes[fileIndex];

                if (currentByte == 0x00) {
                    zeroBytes++;
                    if (zeroBytes == 4) {
                        // Add new line to the script file object
                        if (sequenceLength == sequenceArray.size()) {
                            scriptFile.addLanguageLine(getLineFromCurrentSequence(sequenceArray, sequenceStartIndex));
                        }

                        // Reset current sequence
                        zeroBytes = 0;
                        sequenceLength = 0;
                        sequenceArray.clear();
                    }
                } else {
                    zeroBytes = 0;

                    // If it is the first byte in the sequence, try to read it as the sequence length
                    if (sequenceArray.isEmpty()) {
                        sequenceLength = currentByte;
                    }

                    // Replace new line characters with 0xFF
                    if (currentByte == 0x0A) {
                        sequenceArray.add((byte) 0xFF);
                        continue;
                    }

                    sequenceArray.add(currentByte);
                }
            }
            if (sequenceLength == sequenceArray.size()) {
                scriptFile.addLanguageLine(getLineFromCurrentSequence(sequenceArray, sequenceStartIndex));
            }
        } catch (Exception e) {
            System.out.println("An error occurred reading the file");
            return null;
        }
        return scriptFile;
    }

    // Returns a new LanguageLine from the current sequence
    private static ScriptFile.DataSequence getLineFromCurrentSequence(ArrayList<Byte> currentSequence, int sequenceStartIndex) {
        // Convert sequence ArrayList to byte array
        byte[] data = new byte[currentSequence.size()];

        // Starting at index 1 to skip first "sequence length" byte
        for(int i = 1; i < currentSequence.size(); i++) {
            data[i-1] = currentSequence.get(i);
        }

        // Add new line to the script file object
        return new ScriptFile.DataSequence(data, sequenceStartIndex);
    }
}