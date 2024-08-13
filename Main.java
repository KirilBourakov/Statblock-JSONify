import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main{
    private static Boolean ReadingStatblock = false;
    private static String line = "";
    private static String lastline = "";

    public static void main(String[] args){
        try {
            File file = new File(args[0]);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                line = reader.nextLine().strip();
                UpdateReadingStatus();
                if (ReadingStatblock){
                    System.out.println(line);
                }
                lastline = line;
            }
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
        Boolean emptyline = line.length() == 0;
        Boolean statblockEnd = (line.length() > 1 && line.charAt(0) != '>') || emptyline; 
        if (statblockEnd){
            ReadingStatblock = false;
        }
    }
}