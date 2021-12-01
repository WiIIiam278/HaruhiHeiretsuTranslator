package me.William278.haruhiheiretsutranslator.parser.utils;

import me.William278.haruhiheiretsutranslator.parser.DataFile;
import me.William278.haruhiheiretsutranslator.parser.formats.DialogueScriptFile;
import me.William278.haruhiheiretsutranslator.parser.formats.SystemTextScriptFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/*
 * This class reads in script files for Suzumiya Haruhi no Heiretsu
 * Script files are ones that contain in-game dialogue, located in 3.bin in .BLN Subs 75-297 within the MCB filesystem
 */
public class DataFileReader {

    public static DataFile readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            final byte[] fileBytes = DataFileReader.readToByteArray(filePath);

            return switch (getFileType(fileBytes)) {
                case DIALOGUE_SCRIPT -> DialogueScriptFile.readFile(fileBytes, file.getName(), filePath, file.getParentFile().getName());
                case SYSTEM_TEXT_SCRIPT_FILE -> SystemTextScriptFile.readFile(fileBytes, file.getName(), filePath, file.getParentFile().getName());
                case BINARY -> new DataFile(fileBytes, file.getName(), filePath, file.getParentFile().getName(), DataFile.FileType.BINARY);
            };
        } catch (IOException e) {
            return null;
        }
    }

    private static DataFile.FileType getFileType(byte[] fileBytes) {
        // Dialogue Script - Check for ROOM TIME header at top of file
        byte[] dialogueScriptFilePattern = new byte[] {
                0x05, 0x52, 0x4F, 0x4F, 0x4D, 0x00, 0x00,
                0x00, 0x00, 0x05, 0x54, 0x49, 0x4D, 0x45};
        if (fileBytes.length >= dialogueScriptFilePattern.length) {
            int matchedBytes = 0;
            for (byte fileByte : fileBytes) {
                if (fileByte == dialogueScriptFilePattern[matchedBytes]) {
                    matchedBytes++;
                    if (matchedBytes == dialogueScriptFilePattern.length) {
                        return DataFile.FileType.DIALOGUE_SCRIPT;
                    }
                } else {
                    matchedBytes = 0;
                }
            }
        }

        // Dialogue script
        byte[] wiiEditorFilePattern = new byte[] {
                0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x13, (byte) 0xE8,
                0x00, 0x00, 0x00, 0x34, 0x00, 0x00, 0x00, 0x34};
        byte[] configTextPattern = new byte[] {
                0x00, 0x00, 0x00, 0x0E, 0x00, 0x00, 0x14, 0x14,
                0x00, 0x00, 0x00, 0x7C, 0x00, 0x00, 0x00, 0x7C
        };
        if (fileBytes.length >= wiiEditorFilePattern.length) {
            int matchedBytes = 0;
            for (byte fileByte : fileBytes) {
                if (fileByte == wiiEditorFilePattern[matchedBytes] || fileByte == configTextPattern[matchedBytes]) {
                    matchedBytes++;
                    if (matchedBytes == wiiEditorFilePattern.length) {
                        return DataFile.FileType.SYSTEM_TEXT_SCRIPT_FILE;
                    }
                } else {
                    matchedBytes = 0;
                }
            }
        }

        return DataFile.FileType.BINARY;
    }

    public static byte[] readToByteArray(String filePath) throws IOException {
        // Get the file at the path; return null if it does not exist
        File file = new File(filePath);
        if (!file.exists()) {
            return new byte[0];
        }
        return Files.readAllBytes(file.toPath());
    }

    // Convert a List<Byte> to Byte array
    public static byte[] listToByteArray(List<Byte> byteArray) {
        // Convert sequence ArrayList to byte array
        byte[] data = new byte[byteArray.size()];

        // Starting at index 1 to skip first "sequence length" byte
        for (int i = 0; i < byteArray.size(); i++) {
            data[i] = byteArray.get(i);
        }
        return data;
    }

}