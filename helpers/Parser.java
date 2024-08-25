package helpers;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Parser {
    public String[] splitBeforeChar(String initial, String target){
        int splitIndex = initial.lastIndexOf(target);
        String[] finalList = {"", ""};
        if (splitIndex != -1){
            finalList[0] = initial.substring(0, splitIndex-1).trim();
            finalList[1] = initial.substring(splitIndex).trim();
        }
        if (finalList[0].length() == 0){
            finalList[0] = initial;
        }

        return finalList;
    }

    public String ReplaceNonAlphaNumeric(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }

    public String RemoveNonNumeric(String input){
        return input.replaceAll("[^\\d.]", "");
    }

    public boolean isNumeric(String str) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }
    
    public String getSaveSectionLine(String sectionTitle, ArrayList<String> saveSection){
        String finalSection = "";
        for (String section : saveSection) {
            if (section.toLowerCase().contains(sectionTitle.toLowerCase().strip())){
                finalSection = section;
                break;
            }
        }
        return finalSection;
    }
    public HashMap<String, String> SkillsAndSavesParser(String title, String line){
        HashMap<String, String> finalMap = new HashMap<>();
        if (line.length() != 0){
            line = this.ReplaceNonAlphaNumericNotAddOrSubtract(line).toUpperCase();
            title = title.toUpperCase();

            line = line.substring(line.indexOf(title) + title.length()).trim();
            String[] savelist = line.split("\\s+");

            for (int i = 0; i < savelist.length; i += 2) {
                String key = savelist[i];
                String value = savelist[i+1];
                finalMap.put(key, value);
            }   
        }
        return finalMap;
    }  

    public ArrayList<String> PunctuationSplitter(String title, String line){
        if (line.length() == 0){
            return new ArrayList<>();
        }
        title = title.toUpperCase();
        line = line.substring(line.toUpperCase().indexOf(title) + title.length()).trim();
        ArrayList<String> finalList =  new ArrayList<>(Arrays.asList(line.split("\\p{Punct}")));
        finalList = finalList.stream().map(String::strip).map(String::toLowerCase).filter(s -> !s.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        return finalList;
    }

    private String ReplaceNonAlphaNumericNotAddOrSubtract(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}+\\-]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }
}
