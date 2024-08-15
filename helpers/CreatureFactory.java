package helpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CreatureFactory {
    private int linecount;
    private int section;
    private Creature monster;

    private final ArrayList<String> alignments = new ArrayList<>(Arrays.asList("neu", "law", "cha", "un", "any"));

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

    public boolean HasInformation(){
        return linecount > 0;
    }

    public void addtoSection(String line){
        linecount++;
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

    public Creature Construct(){
        this.ConstructHeaders();
        this.ConstructHpSection();
        this.ConstructStats();
        this.ConstructSaveSection();
        this.ConstructTraits();
        monster.print();

        return this.monster;
    }

    private void ConstructHeaders(){
        ArrayList<String> finalHeaderList = new ArrayList<>();
        for (String header : headerSection) {
            finalHeaderList.add(this.ReplaceNonAlphaNumeric(header).strip());
        }
        String unparsedType = finalHeaderList.get(1);
        String foundCreatureType = "";
        String foundAlignment = "";

        for (String alignment : alignments) {
            int splitIndex = unparsedType.lastIndexOf(alignment);
            if (splitIndex != -1){
                foundCreatureType = unparsedType.substring(0, splitIndex-1).trim();
                foundAlignment = unparsedType.substring(splitIndex).trim();
                break;
            }
        }

        if (foundCreatureType.length() == 0){
            foundCreatureType = "Creature Type Unknown";
        }
        if (foundAlignment.length() == 0){
            foundAlignment = "unaligned";
        }
        
        finalHeaderList.add(1, foundCreatureType);
        finalHeaderList.add(2, foundAlignment);

        monster.setHeaders(finalHeaderList);
    }

    private void ConstructHpSection(){
        ArrayList<String> cleanHPSectionList = new ArrayList<>();
        for (String sectionSlice : hpSection) {
            int fi = sectionSlice.indexOf("**");
            int cutpoint = sectionSlice.indexOf("**", fi + "**".length());
            String finalHPSection = sectionSlice.substring(cutpoint+2);
            cleanHPSectionList.add(finalHPSection.strip());
        }

        ArrayList<String> finalHPSectionList = new ArrayList<>();

        String[] splitAC = this.splitBeforeChar(cleanHPSectionList.get(0), "(");
        finalHPSectionList.add(RemoveNonNumeric(splitAC[0]));
        finalHPSectionList.add(splitAC[1]);

        String[] splitHp = this.splitBeforeChar(cleanHPSectionList.get(1), "(");
        finalHPSectionList.add(RemoveNonNumeric(splitHp[0]));
        finalHPSectionList.add(splitHp[1]);

        finalHPSectionList.add(cleanHPSectionList.get(2));

        monster.setHpSection(finalHPSectionList);
    }

    private String[] splitBeforeChar(String initial, String target){
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
        
        HashMap<String, Integer> finalSaves = this.SkillsAndSavesParser("throws", getSaveSectionLine("saving"));
        HashMap<String, Integer> finalSkills = this.SkillsAndSavesParser("skills", getSaveSectionLine("skill"));
        ArrayList<String> DR = this.CommaSeperatedParser("resistances", getSaveSectionLine("resistance"));
        ArrayList<String> DI = this.CommaSeperatedParser("Immunities", getSaveSectionLine("age im"));
        ArrayList<String> CI = this.CommaSeperatedParser("Immunities", getSaveSectionLine("condition"));
        ArrayList<String> senses = this.CommaSeperatedParser("Senses", getSaveSectionLine("sense"));
        ArrayList<String> languages = this.CommaSeperatedParser("Languages", getSaveSectionLine("lang"));
        
        String unparsedCR = getSaveSectionLine("chall");
        int CR = 0;
        if (unparsedCR.indexOf("(") > 0){
            CR = this.RemoveNonNumericIntify(unparsedCR.substring(0, unparsedCR.indexOf("(")));
        }
        CR = this.RemoveNonNumericIntify(unparsedCR);

        monster.setSavesSection(finalSaves, finalSkills, DR, DI, CI, senses, languages, CR);
    }

    private String getSaveSectionLine(String sectionTitle){
        String finalSection = "";
        for (String section : saveSection) {
            if (section.toLowerCase().contains(sectionTitle.toLowerCase().strip())){
                finalSection = section;
                break;
            }
        }
        return finalSection;
    }

    private HashMap<String, Integer> SkillsAndSavesParser(String title, String line){
        HashMap<String, Integer> finalMap = new HashMap<>();
        if (line.length() != 0){
            line = ReplaceNonAlphaNumeric(line).toUpperCase();
            title = title.toUpperCase();

            line = line.substring(line.indexOf(title) + title.length()).trim();
            String[] savelist = line.split("\\s+");

            for (int i = 0; i < savelist.length; i += 2) {
                String key = savelist[i];
                int value = Integer.parseInt(savelist[i+1]);
                finalMap.put(key, value);
            }   
        }
        return finalMap;
    }   

    private ArrayList<String> CommaSeperatedParser(String title, String line){
        if (line.length() == 0){
            return new ArrayList<>();
        }
        
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

    private String ReplaceNonAlphaNumeric(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }
    private int RemoveNonNumericIntify(String input){
        return Integer.parseInt(input.replaceAll("[^\\d.]", ""));
    }
    private String RemoveNonNumeric(String input){
        return input.replaceAll("[^\\d.]", "");
    }
}
