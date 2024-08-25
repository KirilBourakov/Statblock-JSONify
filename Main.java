import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import helpers.JSONwriter;
import helpers.NodeWriter;

public class Main{
    private static Boolean ReadingStatblock = false;
    private static Boolean finishedReading = false;
    private static String line = "";
    private static String lastline = "";
    private static Creature.CreatureFactory CurrentCreature = new Creature.CreatureFactory();
    private static ArrayList<Creature.CreatureManager> Creatures = new ArrayList<>();

    public static void main(String[] args){
        try {
            File file = new File(args[0]);
            Scanner reader = new Scanner(file);
            NodeWriter writer = new NodeWriter(args[1], args[0], null);
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
                writer.finish();
            }
            writer.finish();
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("No such file exists");
            e.printStackTrace();
        }
    }

    private static void UpdateReadingStatus(){
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