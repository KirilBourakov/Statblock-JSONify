package helpers;

import java.text.NumberFormat;
import java.text.ParsePosition;

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
}
