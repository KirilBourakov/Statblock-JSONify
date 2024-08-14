import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import helpers.CreatureFactory;
import helpers.Creature;

public class Main{
    private static Boolean ReadingStatblock = false;
    private static Boolean finishedReading = false;
    private static String line = "";
    private static String lastline = "";
    private static helpers.CreatureFactory CurrentCreature = new helpers.CreatureFactory();
    private static ArrayList<helpers.Creature> Creatures = new ArrayList<>();

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
                    CurrentCreature = new helpers.CreatureFactory();
                }
                lastline = line;
            }
            if (CurrentCreature.HasInformation()){
                Creatures.add(CurrentCreature.Construct());
            }
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