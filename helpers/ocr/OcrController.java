package helpers.ocr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OcrController {
    private static String pathToPython;

    public static void read(String filepath){
        System.out.println(pathToPython);
        ProcessBuilder processBuilder = new ProcessBuilder(pathToPython, "helpers/ocr/reader_script.py", filepath);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // TODO handle lines. Have script write into a text?
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
            // TODO: handle exitCode
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setup(){
        ProcessBuilder processBuilder = new ProcessBuilder("python", "helpers/ocr/setup_script.py");

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (isFile(line)) {
                    pathToPython = line;
                }
            }
            
            // Wait for the process to complete
            // TODO: handle errors, and clean up how python treats them
            int exitCode = process.waitFor();    
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFile(String pathString) {
        Path path = Paths.get(pathString);
        return Files.isRegularFile(path);
    }
    
}
