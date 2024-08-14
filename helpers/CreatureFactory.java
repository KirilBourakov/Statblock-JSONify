package helpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CreatureFactory {
    private int linecount;
    private int section;
    private Creature monster;

    private ArrayList<String> headerSection;
    private ArrayList<String> hpSection;
    private ArrayList<String> statsSection; 
    private ArrayList<String> saveSection;
    private ArrayList<String> traitsSection;

    public CreatureFactory(){
        linecount = 0;
        section = 1;
        monster = new Creature();

        
        headerSection = new ArrayList<>();
        hpSection = new ArrayList<>();
        statsSection = new ArrayList<>();
        saveSection = new ArrayList<>();
        traitsSection = new ArrayList<>();
    }

    public void addtoSection(String line){
        line = line.substring(1);
        if (line.replace(" ", "").equals("___")){
            section++;
            return;
        }
        switch (section) {
            case 1:
                headerSection.add(line);
                break;
            case 2:
                hpSection.add(line);
                break;
            case 3:
                statsSection.add(line);
                break;
            case 4:
                saveSection.add(line);
                break;
            case 5:
                traitsSection.add(line);
                break;
            default:
                break;
        }
    }

    public void Construct(){
        this.ConstructHeaders();
        this.ConstructHpSection();
        this.ConstructStats();
        this.ConstructSaveSection();
        this.ConstructTraits();
        monster.print();
    }

    private void ConstructHeaders(){
        ArrayList<String> finalHeaderList = new ArrayList<>();
        for (String header : headerSection) {
            finalHeaderList.add(this.ReplaceNonAlphaNumeric(header).strip());
        }

        monster.setHeaders(finalHeaderList);
    }

    private void ConstructHpSection(){
        ArrayList<String> finalHPSectionList = new ArrayList<>();
        for (String sectionSlice : hpSection) {
            int fi = sectionSlice.indexOf("**");
            int cutpoint = sectionSlice.indexOf("**", fi + "**".length());
            String finalHPSection = sectionSlice.substring(cutpoint+2);
            finalHPSectionList.add(finalHPSection.strip());
        }
        monster.setHpSection(finalHPSectionList);
    }

    private void ConstructStats(){
        String statsStr = statsSection.get(2);
        String[] parsedStats = statsStr.split("\\)");

        ArrayList<Integer> finalStats = new ArrayList<>();
        for (String stat : parsedStats){
            stat = stat.replaceAll("\\|", "");
            if (stat.length() > 1) {
                stat = stat.substring(0, stat.indexOf('(')).replaceAll(" ", "");
                try {
                    finalStats.add(Integer.parseInt(stat));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            }
        }
        monster.setStatsSection(finalStats);
    }

    private void ConstructSaveSection(){
        HashMap<String, Integer> finalSaves = this.SkillsAndSavesParser("throws", saveSection.get(0));
        HashMap<String, Integer> finalSkills = this.SkillsAndSavesParser("skills", saveSection.get(1));
        ArrayList<String> DR = this.CommaSeperatedParser("resistances", saveSection.get(2));
        ArrayList<String> DI = this.CommaSeperatedParser("Immunities", saveSection.get(3));
        ArrayList<String> CI = this.CommaSeperatedParser("Immunities", saveSection.get(4));
        ArrayList<String> senses = this.CommaSeperatedParser("Senses", saveSection.get(5));
        ArrayList<String> languages = this.CommaSeperatedParser("Languages", saveSection.get(6));
        
        String unparsedChallange = saveSection.get(7);
        int CR = this.RemoveNonNumericIntify(unparsedChallange.substring(0, unparsedChallange.indexOf("(")));

        monster.setSavesSection(finalSaves, finalSkills, DR, DI, CI, senses, languages, CR);
    }

    private HashMap<String, Integer> SkillsAndSavesParser(String title, String line){
        HashMap<String, Integer> finalMap = new HashMap<>();

        line = ReplaceNonAlphaNumeric(line).toUpperCase();
        title = title.toUpperCase();
        
        line = line.substring(line.indexOf(title) + title.length()).trim();
        String[] savelist = line.split("\\s+");
        for (int i = 0; i < savelist.length; i += 2) {
            String key = savelist[i];
            int value = Integer.parseInt(savelist[i+1]);
            finalMap.put(key, value);
        }   
        return finalMap;
    }   

    private ArrayList<String> CommaSeperatedParser(String title, String line){
        line = ReplaceNonAlphaNumeric(line).toUpperCase();
        title = title.toUpperCase();
        line = line.substring(line.indexOf(title) + title.length()).trim();
        ArrayList<String> finalList =  new ArrayList<>(Arrays.asList(line.split("\\s+")));
        return finalList;
    }

    private void ConstructTraits(){
        ArrayList<String> cleanTraits = new ArrayList<>();
        boolean previousHeader = false;
        for (String traitLine : traitsSection) {
            if (traitLine.length() != 0){
                if (cleanTraits.size() == 0){
                    cleanTraits.add(traitLine);
                } else{
                    if (previousHeader){
                        cleanTraits.add(traitLine);
                    } else if (traitLine.contains("#")){
                        cleanTraits.add(traitLine);
                        previousHeader = true;
                    } else if (traitLine.contains("*")){
                        cleanTraits.add(traitLine);
                    } else {
                        String lastTrait = cleanTraits.get(cleanTraits.size() - 1);
                        cleanTraits.set(cleanTraits.size() - 1, lastTrait + traitLine);
                    }
                }
            }
        }

        HashMap<String, ArrayList<String>> TypeMap = new HashMap<>();
        String inputPoint = "traits";
        for (String trait : cleanTraits) {
            if (trait.contains("#")){
                String comp = trait.toLowerCase();
                if (comp.contains("bonus")){
                    inputPoint = "bonusAction";
                } else if (comp.contains("reaction")){
                    inputPoint = "reactions";
                } else if (comp.contains("legendary")){
                    inputPoint = "LActions";
                } else if (comp.contains("mythic")){
                    inputPoint = "mythicActions";
                } else if (comp.contains("action")){
                    inputPoint = "actions";
                }
            } else {
                if (TypeMap.containsKey(inputPoint)) {
                    ArrayList<String> in = TypeMap.get(inputPoint);
                    in.add(trait);
                } else {
                    ArrayList<String> in = new ArrayList<>();
                    in.add(trait);
                    TypeMap.put(inputPoint, in);
                }
            }
        }
        monster.setTratsSection(TypeMap);
    }

    private int Contains(String strInput, String query){
        int originalLen = strInput.length();
        strInput.replaceAll(query, "");
        int newLen = strInput.length();
        return  (originalLen - newLen);
    }

    private String ReplaceNonAlphaNumeric(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }
    private int RemoveNonNumericIntify(String input){
        return Integer.parseInt(input.replaceAll("[^\\d.]", ""));
    }
}
