package helpers;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        String unparsedTypeWithoutAlignment = "";
        String foundAlignment = "";

        for (String alignment : alignments) {
            int splitIndex = unparsedType.lastIndexOf(alignment);
            if (splitIndex != -1){
                unparsedTypeWithoutAlignment = unparsedType.substring(0, splitIndex-1).trim();
                foundAlignment = unparsedType.substring(splitIndex).trim();
                break;
            }
        }

        if (unparsedTypeWithoutAlignment.length() == 0){
            unparsedTypeWithoutAlignment = "unknown unknown";
        }
        if (foundAlignment.length() == 0){
            foundAlignment = "unaligned";
        }


        HashMap<String, String> finalMap = new HashMap<>();
        finalMap.put("name", finalHeaderList.get(0));
        finalMap.put("alignment", foundAlignment);

        String[] finalType = unparsedTypeWithoutAlignment.split(" ");
        if (finalType.length >= 1) {
            finalMap.put("size", finalType[0]);
        } else {
            finalMap.put("size", "unknown");
        }

        if (finalType.length >= 2) {
            finalMap.put("type", finalType[1]);
        } else {
            finalMap.put("type", "unknown");
        }

        ArrayList<String> tags = new ArrayList<>();
        if (finalType.length > 2){
            String[] tagsList = Arrays.copyOfRange(finalType, 2, finalType.length);
            tags = new ArrayList<>(Arrays.asList(tagsList));
        }

        monster.setHeaders(finalMap, tags);
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

        String[] unparsedSpeed = cleanHPSectionList.get(2).split(" ");
        HashMap<String, Integer> speedMap = new HashMap<>();

        String speedType = "walk";
        for (String speedPortion : unparsedSpeed) {
            if(!ReplaceNonAlphaNumeric(speedPortion).equalsIgnoreCase("ft")){
                if (isNumeric(speedPortion)){
                    speedMap.put(speedType, RemoveNonNumericIntify(speedPortion));
                } else {
                    speedType = speedPortion;
                }
            }
        }

        monster.setHpSection(finalHPSectionList, speedMap);
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
        ArrayList<String> DR = this.PunctuationSplitter("resistances", getSaveSectionLine("resistance"));
        ArrayList<String> DI = this.PunctuationSplitter("Immunities", getSaveSectionLine("age im"));
        ArrayList<String> CI = this.PunctuationSplitter("Immunities", getSaveSectionLine("condition"));
        ArrayList<String> languages = this.PunctuationSplitter("Languages", getSaveSectionLine("lang"));
        
        ArrayList<String> cleanSenses = this.PunctuationSplitter("Senses", getSaveSectionLine("sense"));
        ArrayList<String> senses = new ArrayList<>(cleanSenses.stream().filter(sense -> !sense.toLowerCase().contains("percep")).collect(Collectors.toList()));

        int passive = cleanSenses.stream().filter(sense -> sense.toLowerCase().contains("percep")).mapToInt(this::RemoveNonNumericIntify).findFirst().orElse(0);

        String unparsedCR = getSaveSectionLine("chall");
        int CR = 0;

        if (unparsedCR.indexOf("(") > 0){
            CR = this.RemoveNonNumericIntify(unparsedCR.substring(0, unparsedCR.indexOf("(")));
        } else {
            CR = this.RemoveNonNumericIntify(unparsedCR);
        }

        monster.setSavesSection(finalSaves, finalSkills, DR, DI, CI, senses, passive, languages, CR);
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

    private ArrayList<String> PunctuationSplitter(String title, String line){
        if (line.length() == 0){
            return new ArrayList<>();
        }
        
        title = title.toUpperCase();
        line = line.substring(line.toUpperCase().indexOf(title) + title.length()).trim();
        ArrayList<String> finalList =  new ArrayList<>(Arrays.asList(line.split("\\p{Punct}")));
        finalList = finalList.stream().map(String::strip).filter(s -> !s.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        return finalList;
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

    private boolean isNumeric(String str) {
         ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }
}
