package org.example.helpers;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Parser {
    public static String[] splitBeforeChar(String initial, String target){
        int splitIndex = initial.lastIndexOf(target);
        String[] finalList = {"", ""};
        if (splitIndex != -1){
            finalList[0] = initial.substring(0, splitIndex-1).trim();
            finalList[1] = initial.substring(splitIndex).trim();
        }
        if (finalList[0].isEmpty()){
            finalList[0] = initial;
        }

        return finalList;
    }

    public static String replaceNonAlphaNumeric(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }

    public static String removeNonNumeric(String input){
        return input.replaceAll("[^\\d.]", "");
    }

    public static boolean isNumeric(String str) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }
    
    public static String getSaveSectionLine(String sectionTitle, ArrayList<String> saveSection){
        String finalSection = "";
        for (String section : saveSection) {
            if (section.toLowerCase().contains(sectionTitle.toLowerCase().strip())){
                finalSection = section;
                break;
            }
        }
        return finalSection;
    }
    public static HashMap<String, String> skillsAndSavesParser(String title, String line){
        HashMap<String, String> finalMap = new HashMap<>();
        if (!line.isEmpty()){
            line = ReplaceNonAlphaNumericNotAddOrSubtract(line).toUpperCase();
            title = title.toUpperCase();

            line = line.substring(line.indexOf(title) + title.length()).trim();
            String[] savelist = line.split("\\s+");

            for (int i = 0; i < savelist.length; i += 2) {
                String key = savelist[i].toLowerCase();
                String value = savelist[i+1];
                finalMap.put(key, value);
            }   
        }
        return finalMap;
    }  

    public static ArrayList<String> punctuationSplitter(String title, String line){
        if (line.isEmpty()){
            return new ArrayList<>();
        }
        title = title.toUpperCase();
        line = line.substring(line.toUpperCase().indexOf(title) + title.length()).trim();
        ArrayList<String> finalList =  new ArrayList<>(Arrays.asList(line.split("[\\p{Punct}&&[^']]")));
        finalList = finalList.stream().map(String::strip).map(String::toLowerCase).filter(s -> !s.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        return finalList;
    }

    public static HashMap<String, String> parseATrait(String trait){
        int lastIndex = trait.lastIndexOf("*");
        String name;
        String description; 
        if (lastIndex == -1){
            name = "unknown";
            description = trait;
        } else{
            name = trait.substring(0, lastIndex);
            description = trait.substring(lastIndex + 1);
        }
        return new HashMap<String, String>(){{
            put("name", name.replaceAll("\\*", "").strip());
            put("description", description.strip());
        }};
    }

    public static ArrayList<String> getFirstLetters(String input){
        ArrayList<String> letters = new ArrayList<>();

        String[] words = input.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                letters.add(String.valueOf(word.charAt(0)).toUpperCase());
            }
        }
        return letters;
    }

    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");

        String titlecased = "";
        for (String word : words) {
            if (!word.isEmpty()) {
                String titleCasedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                titlecased = titlecased + titleCasedWord + " ";
            }
        }

        return titlecased.trim();
    }

    // This method handles malformed stats, likely resulting from an OCR failing to read them properly
    // it works by collapsing the stat into it's raw numbers, then starting at the end and backtracking finding first the mod, then the stat that caused it
    public static ArrayList<String> handleMalformedStats(String statsStr){
        ArrayList<String> finalStats = new ArrayList<>();

        // start by turing what we have into a list of numbers
        String statNumbers = replaceNonAlphaNumeric(statsStr).replaceAll("\\s", "");
        
        // starting at the back, where the first modifier is
        int i = statNumbers.length() - 1;
        while (i >= 0) {

            // find the mod
            char c = statNumbers.charAt(i);

            // find the stat
            String stat = "" + statNumbers.charAt(i-1);
            i--;
            if(Character.getNumericValue(c) == Math.abs(roundDownDivision(Integer.parseInt(stat)))){
                finalStats.add(stat);
            } else {
                stat = reverseString(stat + statNumbers.charAt(i-1));
                i--;
                if(Character.getNumericValue(c) == Math.abs(roundDownDivision(Integer.parseInt(stat)))){
                    finalStats.add(stat);
                } else {
                    finalStats.add("30");
                    i--;
                }
            }
            i--;
        }
        // reverse the list as we found cha first and str last
        Collections.reverse(finalStats);
        return finalStats;
    }

    private static int roundDownDivision(int x) {
        x = x - 10;
        if (x >= 0) {
            return x / 2;
        } else {
            return (x - 1) / 2;
        }
    }

    private static String reverseString(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }

    private static String ReplaceNonAlphaNumericNotAddOrSubtract(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}+\\-]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }
}
