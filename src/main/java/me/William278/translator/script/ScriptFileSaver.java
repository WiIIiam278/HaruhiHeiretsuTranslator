package me.William278.translator.script;

import java.io.File;
import java.io.FileOutputStream;

public class ScriptFileSaver {

    public static void saveScript(ScriptFile file, String targetFilePath) {
        try {
            // Make target file
            File targetFile = new File(targetFilePath);
            if (!targetFile.createNewFile()) {
                return;
            }

            // Write to file and complete
            try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
                fileOutputStream.write(file.toByteArray());
            }
        } catch (Exception e) {
            System.out.println("An error occurred reading/saving the file");
            e.printStackTrace();
        }
    }

}
