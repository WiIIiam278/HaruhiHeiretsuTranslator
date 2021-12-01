package me.William278.haruhiheiretsutranslator.parser.utils;

import me.William278.haruhiheiretsutranslator.parser.formats.ScriptFile;

import java.io.File;
import java.io.FileOutputStream;

public class DataFileSaver {

    public static void saveScript(ScriptFile file, String targetFilePath) {
        try {
            // Make target file
            File targetFile = new File(targetFilePath);
            if (!targetFile.createNewFile()) {
                return;
            }

            // Write to file and complete
            try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
                fileOutputStream.write(file.getByteArray());
            }
        } catch (Exception e) {
            System.out.println("An error occurred reading/saving the file");
            e.printStackTrace();
        }
    }

}
