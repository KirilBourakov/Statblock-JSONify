package helpers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.swing.JLabel;

import helpers.writer.NodeWriter;
import helpers.ocr.OcrController;

public class Logic{
    private Boolean ReadingStatblock = false;
    private Boolean finishedReading = false;
    private String line = "";
    private String lastline = "";
    private Creature.CreatureFactory CurrentCreature = new Creature.CreatureFactory();

    public boolean txtToJSON(String inputFile, String outputFile){
        try {
            File file = new File(inputFile);
            Scanner reader = new Scanner(file);
            NodeWriter writer = new NodeWriter(outputFile, file.getName(), null);
            writer.start();
            while (reader.hasNextLine()) {
                line = reader.nextLine().strip();
                textToJSONUpdateReadingStatus();
                if (ReadingStatblock){
                    CurrentCreature.addtoSection(line);
                }
                if (finishedReading){
                    Creature.CreatureManager newCreature = CurrentCreature.Construct();
                    writer.setManager(newCreature);
                    writer.WriteCreature();
                    
                    finishedReading = false;
                    CurrentCreature = new Creature.CreatureFactory();
                }
                lastline = line;
            }
            if (CurrentCreature.HasInformation()){
                Creature.CreatureManager newCreature = CurrentCreature.Construct();
                writer.setManager(newCreature);
                writer.WriteCreature();
            }
            writer.finish();
            reader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("No such file exists");
            e.printStackTrace();
            return false;
        }
    }

    private void textToJSONUpdateReadingStatus(){
        if (line.length() > 1 && lastline.equals("___") && line.charAt(0) == '>'){
            ReadingStatblock = true;
        }
        boolean endOfStatBlockFound = (line.length() == 0 || line.charAt(0) != '>');
        if (ReadingStatblock && endOfStatBlockFound){
            ReadingStatblock = false;
            finishedReading = true;
        }
    }

    public boolean imgToJSON(String input, String outputFile, JLabel converstionStatus){
        converstionStatus.setText("Preparing OCR script...");
        OcrController.setup();

        File in = new File(input);
        ArrayList<String> inputs = new ArrayList<>();
        if (in.isDirectory()) {
            try {
                Path directoryPath = Paths.get(input);
                inputs = new ArrayList<>(
                    Files.list(directoryPath).filter(Files::isRegularFile).map(Path::toAbsolutePath).map(Path::toString).collect(Collectors.toList())
                );
            } catch (IOException e) {
                System.err.println("An error occurred while listing files: " + e.getMessage());
            }
        } else {
            inputs.add(input);
        }

        NodeWriter writer = new NodeWriter(outputFile, in.getName(), null);
        writer.start();
        for (String inputFile : inputs) {
            converstionStatus.setText("Reading " + inputFile + "...");
            ArrayList<String> lines = OcrController.read(inputFile);

            converstionStatus.setText("Converting " + inputFile + "...");

            int lineCount = 0;
            boolean addedStats = false;
            boolean addedCR = false;
            for (String line : lines) {
                line = line.strip();
                if (!line.isBlank()) {
                    // first three lines of a statblock should be header section
                    if (lineCount < 2) {
                        CurrentCreature.addByAssumedSection(line, "headerSection");
                    } 
                    // next three lines of a statblock should be hpSection
                    else if (lineCount < 5) {
                        if (line.toLowerCase().contains("speed")) {
                            line = line.toLowerCase().replace("speed", "**speed**");
                        }
                        CurrentCreature.addByAssumedSection(line, "hpSection");
                    }   

                    // add statsection, which should be one line after hpSection
                    else if (lineCount < 7){
                        CurrentCreature.addByAssumedSection(line, "statsSection");
                        addedStats = true;
                    }

                    // after addingstats, add to saveSection until we see challange rating
                    else if (addedStats && !addedCR){
                        CurrentCreature.addByAssumedSection(line, "saveSection");

                        if (line.toLowerCase().contains("challenge")) {
                            addedCR = true;
                        }
                    }

                    // everything past cr is a trait
                    else {
                        CurrentCreature.addByAssumedSection(line, "traitsSection");
                    }

                    lineCount++;
                }
            }

            Creature.CreatureManager newCreature = CurrentCreature.Construct();
            writer.setManager(newCreature);
            writer.WriteCreature();

            CurrentCreature = new Creature.CreatureFactory();
        }

        writer.finish();

        converstionStatus.setText("Converting Finished!");

        return true;
    }
}