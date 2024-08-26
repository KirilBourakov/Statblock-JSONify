package helpers;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Runner{
    private Boolean ReadingStatblock = false;
    private Boolean finishedReading = false;
    private String line = "";
    private String lastline = "";
    private Creature.CreatureFactory CurrentCreature = new Creature.CreatureFactory();

    public void runLogic(String inputFile, String outputFile){
        try {
            File file = new File(inputFile);
            Scanner reader = new Scanner(file);
            NodeWriter writer = new NodeWriter(outputFile, file.getName(), null);
            writer.start();
            while (reader.hasNextLine()) {
                line = reader.nextLine().strip();
                UpdateReadingStatus();
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
        } catch (FileNotFoundException e) {
            System.out.println("No such file exists");
            e.printStackTrace();
        }
    }

    private void UpdateReadingStatus(){
        if (line.length() > 1 && lastline.equals("___") && line.charAt(0) == '>'){
            ReadingStatblock = true;
        }
        boolean endOfStatBlockFound = (line.length() == 0 || line.charAt(0) != '>');
        if (ReadingStatblock && endOfStatBlockFound){
            ReadingStatblock = false;
            finishedReading = true;
        }
    }
}