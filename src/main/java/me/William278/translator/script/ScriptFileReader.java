package me.William278.translator.script;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/*
 * This class reads in script files for Suzumiya Haruhi no Heiretsu
 * Script files are ones that contain in-game dialogue, located in 3.bin in .BLN Subs 75-297 within the MCB filesystem
 */
public class ScriptFileReader {

    public static byte[] readToByteArray(String filePath) throws IOException {
        // Get the file at the path; return null if it does not exist
        File file = new File(filePath);
        if (!file.exists()) {
            return new byte[0];
        }
        return Files.readAllBytes(file.toPath());
    }

    public static ScriptFile readFile(String filePath) {
        ScriptFile scriptFile = new ScriptFile();
        try {
            // Load the file into a byte array
            final byte[] fileBytes = readToByteArray(filePath);

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
                    final List<Byte> sequenceToAdd = currentSequence.subList(0, currentSequence.size()-4);
                    final List<Byte> zeroesToAdd = currentSequence.subList(currentSequence.size()-4, currentSequence.size());

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

        } catch (IOException e) {
            System.out.println("An error occurred reading the file");
            e.printStackTrace();
            return null;
        }
        return scriptFile;
    }

    public static byte[] toByteArray(List<Byte> byteArray) {
        // Convert sequence ArrayList to byte array
        byte[] data = new byte[byteArray.size()];

        // Starting at index 1 to skip first "sequence length" byte
        for (int i = 0; i < byteArray.size(); i++) {
            data[i] = byteArray.get(i);
        }
        return data;
    }

    // Get a DataItem from a byte array and assign it a flag if it is a text / information sequence
    private static ScriptFile.DataItem getDataItem(List<Byte> currentSequence, boolean isSequence) {
        // Convert sequence ArrayList to byte array
        byte[] data = toByteArray(currentSequence);

        return new ScriptFile.DataItem(data, isSequence);
    }

}