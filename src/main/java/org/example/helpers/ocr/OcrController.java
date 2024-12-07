package org.example.helpers.ocr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OcrController {
    public static ArrayList<String> read(String filepath){
        String path = Paths.get(System.getProperty("user.dir"), "target/scripts/ocr/reader.py").toString();
        String pathToPython =  Paths.get(System.getProperty("user.dir"), "ocr_venv/Scripts/python.exe").toString();
        ProcessBuilder processBuilder = new ProcessBuilder(pathToPython, path, filepath);

        System.out.println("BELOWWW");
        System.out.println(pathToPython);
        System.out.println(path);
        System.out.println(filepath);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            ArrayList<String> lines = new ArrayList<>();
            System.out.println("READING>......................");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lines.add(line);
            }
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            return lines;
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static int setup(){
        String path = Paths.get(System.getProperty("user.dir"), "target/scripts/ocr/setup.py").toString();
        ProcessBuilder processBuilder = new ProcessBuilder("python", path);
        System.out.println("path: " + path);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
//                if (isFile(line)) {
//                    pathToPython = line;
//                }
            }
            
            // Wait for the process to complete
            // TODO: handle errors, and clean up how python treats them
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 3;
    }

    private static boolean isFile(String pathString) {
        Path path = Paths.get(pathString);
        return Files.isRegularFile(path);
    }
    
}
