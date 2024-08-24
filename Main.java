import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import helpers.JSONwriter;

public class Main{
    private static Boolean ReadingStatblock = false;
    private static Boolean finishedReading = false;
    private static String line = "";
    private static String lastline = "";
    private static Creature.CreatureFactory CurrentCreature = new Creature.CreatureFactory();
    private static ArrayList<Creature.CreatureManager> Creatures = new ArrayList<>();
    private static JSONwriter writer;

    public static void main(String[] args){
        try {
            File file = new File(args[0]);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                line = reader.nextLine().strip();
                UpdateReadingStatus();
                if (ReadingStatblock){
                    CurrentCreature.addtoSection(line);
                }
                if (finishedReading){
                    Creatures.add(CurrentCreature.Construct());
                    finishedReading = false;
                    CurrentCreature = new Creature.CreatureFactory();
                }
                lastline = line;
            }
            if (CurrentCreature.HasInformation()){
                Creatures.add(CurrentCreature.Construct());
            }
            // writer = new JSONwriter(Creatures, args[1], file.getName().replaceFirst("[.][^.]+$", ""));
            // writer.WriteCreatures();

            System.out.println(Creatures);
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