package me.William278.haruhiheiretsutranslator.parser.formats;

import java.util.ArrayList;

/*
 * Files that contain system script (i.e. Save data management, config options, etc)
 */
public class SystemTextScriptFile extends ScriptFile {

    public SystemTextScriptFile(byte[] rawData, String fileName, String filePath, String parentFileName) {
        super(rawData, fileName, filePath, parentFileName, FileType.SYSTEM_TEXT_SCRIPT_FILE);
    }

    public static SystemTextScriptFile readFile(byte[] fileBytes, String fileName, String filePath, String fileParentName) {
        // Create a new ScriptFile to return
        SystemTextScriptFile scriptFile = new SystemTextScriptFile(fileBytes, fileName, filePath, fileParentName);

        // Return null if the file could not be read
        if (fileBytes.length == 0) {
            return null;
        }

        // Initialize byte iteration flags
        ArrayList<Byte> currentSequence = new ArrayList<>(); // The current sequence
        boolean isZeroSequence = false;

        // Iterate through every byte
        for (byte currentByte : fileBytes) {
            if (currentByte == 0x00 || currentByte == (byte) 0xFF || currentByte == (byte) 0xF0 ||  currentByte == (byte) 0x80) {
                if (!isZeroSequence) {
                    scriptFile.addDataItem(getDataItem(currentSequence, (currentSequence.size() > 2)));
                    currentSequence = new ArrayList<>();

                    isZeroSequence = true;
                }
            } else {
                if (isZeroSequence) {
                    scriptFile.addDataItem(getDataItem(currentSequence, false));
                    currentSequence = new ArrayList<>();

                    isZeroSequence = false;
                }
            }
            currentSequence.add(currentByte);
        }
        scriptFile.addDataItem(getDataItem(currentSequence, !isZeroSequence));

        // Return the script file
        return scriptFile;
    }

}
