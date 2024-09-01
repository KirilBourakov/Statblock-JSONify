package org.example.Creature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.example.helpers.Parser;

// TODO: Rework size and header parsing in general.
public class CreatureFactory {
    private int linecount;
    private int section;
    private CreatureManager creature;

    private final ArrayList<String> alignments = new ArrayList<>(Arrays.asList("neu", "law", "cha", "un", "any"));

    private ArrayList<String> headerSection;
    private ArrayList<String> hpSection;
    private ArrayList<String> statsSection; 
    private ArrayList<String> saveSection;
    private ArrayList<String> traitsSection;

    public CreatureFactory(){
        linecount = 0;
        section = 1;
        creature = new CreatureManager();

        
        headerSection = new ArrayList<>();
        hpSection = new ArrayList<>();
        statsSection = new ArrayList<>();
        saveSection = new ArrayList<>();
        traitsSection = new ArrayList<>();
    }

    public boolean hasInformation(){
        return linecount > 0;
    }

    public void addToSection(String line){
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

    public void addByAssumedSection(String line, String section){
        switch (section) {
            case "headerSection":
                headerSection.add(line);
                break;
            case "hpSection":
                hpSection.add(line);
                break;
            case "statsSection":
                statsSection.add(line);
                break;
            case "saveSection":
                saveSection.add(line);
                break;
            case "traitsSection":
                traitsSection.add(line);
                break;
            default:
                break;
        }
    }

    public CreatureManager construct(){
        // System.out.println(this.headerSection);
        // System.out.println(this.hpSection);
        // System.out.println(this.statsSection);
        // System.out.println(this.saveSection);
        // System.out.println(this.traitsSection);

        this.constructHeaders();
        this.constructHpSection();
        this.constructStats();
        this.constructSaveSection();
        this.constructTraits();
        // creature.print(null, 0);

        return this.creature;
    }

    private void constructHeaders(){
        ArrayList<String> finalHeaderList = new ArrayList<>();
        for (String header : headerSection) {
            finalHeaderList.add(Parser.replaceNonAlphaNumeric(header).strip().toLowerCase());
        }
        System.out.println(finalHeaderList);
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

        if (unparsedTypeWithoutAlignment.isEmpty()){
            unparsedTypeWithoutAlignment = "unknown unknown";
        }
        if (foundAlignment.isEmpty()){
            foundAlignment = "unaligned";
        }


        HashMap<String, String> finalMap = new HashMap<>();
        finalMap.put("name", finalHeaderList.getFirst());
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

        // insert
        creature.insertStringNode("name", Parser.toTitleCase(finalMap.get("name")), true);
        creature.insertLiteralList("size", Parser.getFirstLetters(finalMap.get("size")), true);
        creature.insertLiteralList("alignment", Parser.getFirstLetters(finalMap.get("alignment")), true);

        // insert type
        if (!tags.isEmpty()){
            CreatureNode typeNode = new CreatureNode("type", finalMap.get("type"), true);
            CreatureNode tagNode = creature.createLiteralList("tags", tags, true);
            typeNode.setChild(tagNode);
            creature.insertCreatedNode("type", typeNode);
        } else {
            creature.insertStringNode("type", finalMap.get("type"), true);
        }
    }

    private void constructHpSection(){
        ArrayList<String> cleanHPSectionList = new ArrayList<>();
        for (String sectionSlice : hpSection) {
            int fi = sectionSlice.indexOf("**");
            int cutpoint = sectionSlice.indexOf("**", fi + "**".length());
            String finalHPSection = sectionSlice.substring(cutpoint+2);
            cleanHPSectionList.add(finalHPSection.strip());
        }

        // handle AC
        // TODO: handle several AC values
        String[] splitAC = Parser.splitBeforeChar(cleanHPSectionList.get(0), "(");
        String ACvalue = Parser.removeNonNumeric(splitAC[0]);
        String ACtype = Parser.replaceNonAlphaNumeric(splitAC[1]);

        if (!ACtype.isEmpty()){
            CreatureNode ACvalueNode = new CreatureNode("ac", ACvalue, false);
            CreatureNode ACtypeNode = creature.createLiteralList("type", new ArrayList<>(Arrays.asList(ACtype)), true);
            ACvalueNode.setChild(ACtypeNode);
            CreatureNode AcObj = new CreatureNode(null, ACvalueNode);
            creature.insertNodeList("ac", new ArrayList<>(List.of(AcObj)));
        } else {
            creature.insertLiteralList("ac", new ArrayList<>(List.of(ACvalue)), false);
        }

        //handle HP
        String[] splitHp = Parser.splitBeforeChar(cleanHPSectionList.get(1), "(");
        String hpValue = Parser.removeNonNumeric(splitHp[0]);
        String hpFormula = splitHp[1].replace("(", "").replace(")", "");

        CreatureNode hpValueNode = new CreatureNode("average", hpValue, false);
        CreatureNode hpFormulaNode = new CreatureNode("formula", hpFormula, true);
        hpValueNode.setChild(hpFormulaNode);
        creature.insertCreatedNode("hp", hpValueNode);

        //handle speeds
        String[] unparsedSpeed = cleanHPSectionList.get(2).split(" ");
        HashMap<String, String> speedMap = new HashMap<>();
        String speedType = "walk";
        for (String speedPortion : unparsedSpeed) {
            speedPortion = Parser.replaceNonAlphaNumeric(speedPortion).strip();
            if(!speedPortion.equalsIgnoreCase("ft")){
                if (Parser.isNumeric(speedPortion.replace("ft", ""))){
                    speedMap.put(speedType, Parser.removeNonNumeric(speedPortion));
                } 
                //
                else if(!speedPortion.equalsIgnoreCase("ft")){
                    speedType = speedPortion;
                }
            }
        }

        creature.insertFromHashMap("speed", speedMap, false);
    }

    private void constructStats(){
        String statsStr = statsSection.get(statsSection.size()-1);
        
        String[] parsedStats = statsStr.split("\\)");

        ArrayList<String> finalStats = new ArrayList<>();
        try{
            for (String stat : parsedStats){
                stat = stat.replaceAll("\\|", "");
                if (stat.contains("(")) {
                    int indexOfParen = stat.indexOf('(');
                    if (indexOfParen > 0) {
                        stat = stat.substring(0, indexOfParen).replaceAll(" ", "").trim();
                        finalStats.add(stat);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e){
            finalStats = Parser.handleMalformedStats(statsStr);
        }
        if (finalStats.size() != 6) {
            finalStats = Parser.handleMalformedStats(statsStr);
        }

        System.out.println("________________________");
        System.out.println(finalStats);
        
        creature.insertStringNode("str", finalStats.get(0), false);
        creature.insertStringNode("dex", finalStats.get(1), false);
        creature.insertStringNode("con", finalStats.get(2), false);
        creature.insertStringNode("int", finalStats.get(3), false);
        creature.insertStringNode("wis", finalStats.get(4), false);
        creature.insertStringNode("cha", finalStats.get(5), false);
    }

    private void constructSaveSection(){
        // saves
        HashMap<String, String> finalSaves = Parser.skillsAndSavesParser("throws", this.parser.getSaveSectionLine("saving", this.saveSection));
        if (!finalSaves.isEmpty()) {
            creature.insertFromHashMap("save", finalSaves, true);
        }

        // skills
        HashMap<String, String> finalSkills = Parser.skillsAndSavesParser("skills", this.parser.getSaveSectionLine("skill", this.saveSection));
        if (!finalSkills.isEmpty()) {
            creature.insertFromHashMap("skill", finalSkills, true);
        }

        // Damage resistances 
        // TODO: handle conditionals
        ArrayList<String> DR = Parser.punctuationSplitter("resistances", this.parser.getSaveSectionLine("resistance", this.saveSection));
        if (!DR.isEmpty()) {
            creature.insertLiteralList("resist", DR, true);
        }

        // Damage immunities
        // TODO: handle conditionals
        ArrayList<String> DI = Parser.punctuationSplitter("Immunities", this.parser.getSaveSectionLine("age im", this.saveSection));
        if (!DI.isEmpty()) {
            creature.insertLiteralList("immune", DI, true);
        }

        // Condition Immunities
        // TODO: handle conditionals
        ArrayList<String> CI = Parser.punctuationSplitter("Immunities", this.parser.getSaveSectionLine("condition", this.saveSection));
        if (!CI.isEmpty()) {
            creature.insertLiteralList("conditionImmune", CI, true);
        }

        // languages
        ArrayList<String> languages = Parser.punctuationSplitter("Languages", this.parser.getSaveSectionLine("lang", this.saveSection));
        if (!languages.isEmpty()) {
            creature.insertLiteralList("languages", languages, true);
        }

        // senses and passive
        
        ArrayList<String> cleanSenses = Parser.punctuationSplitter("Senses", this.parser.getSaveSectionLine("sense", this.saveSection));
       
        ArrayList<String> senses = new ArrayList<>(cleanSenses.stream().filter(sense -> !sense.toLowerCase().contains("passive")).collect(Collectors.toList()));
        String passive = Parser.removeNonNumeric(cleanSenses.stream().filter(sense -> sense.toLowerCase().contains("passive")).findFirst().orElse("0"));
        if (!senses.isEmpty()) {
            creature.insertLiteralList("senses", senses, true);
        }
        creature.insertStringNode("passive", passive, false);

        // Challenge Rating
        // TODO: handle special CRs (above 30, increase in lair, etc)
        String unparsedCR = Parser.getSaveSectionLine("chall", this.saveSection);
        String CR;
        if (unparsedCR.indexOf("(") > 0){
            CR = Parser.removeNonNumeric(unparsedCR.substring(0, unparsedCR.indexOf("(")));
        } else {
            CR = Parser.removeNonNumeric(unparsedCR);
        }
        creature.insertStringNode("cr", CR, true);
    }

    private void constructTraits(){
        ArrayList<String> cleanTraits = getCleanTraits();

        // Todo: handle mythic headers
        HashMap<String, ArrayList<HashMap<String, String>>> TypeMap = new HashMap<>();
        String inputPoint = "trait";
        for (String trait : cleanTraits) {
            if (trait.contains("#")){
                String comp = trait.toLowerCase();
                if (comp.contains("bonus")){
                    inputPoint = "bonus";
                } else if (comp.contains("reaction")){
                    inputPoint = "reaction";
                } else if (comp.contains("legendary")){
                    inputPoint = "legendary";
                } else if (comp.contains("mythic")){
                    inputPoint = "mythic";
                } else if (comp.contains("action")){
                    inputPoint = "action";
                }
            } else {
                if (TypeMap.containsKey(inputPoint)) {
                    ArrayList<HashMap<String, String>> in = TypeMap.get(inputPoint);
                    in.add(Parser.parseATrait(trait));
                } else {
                    ArrayList<HashMap<String, String>> in = new ArrayList<>();
                    in.add(Parser.parseATrait(trait));
                    TypeMap.put(inputPoint, in);
                }
            }
        }
        creature.insertFromMapListOfMaps(TypeMap);
    }
    private ArrayList<String> getCleanTraits() {
        ArrayList<String> cleanTraits = new ArrayList<>();
        boolean previousHeader = false;
        for (String traitLine : traitsSection) {
            if (!traitLine.isEmpty()){
                if (cleanTraits.isEmpty()){
                    cleanTraits.add(traitLine);
                } else{
                    if (previousHeader){
                        cleanTraits.add(traitLine);
                        previousHeader = false;
                    } else if (traitLine.contains("#")){
                        cleanTraits.add(traitLine);
                        previousHeader = true;
                    } else if (traitLine.contains("*")){
                        cleanTraits.add(traitLine);
                    } else {
                        String lastTrait = cleanTraits.getLast();
                        cleanTraits.set(cleanTraits.size() - 1, lastTrait + traitLine);
                    }
                }
            }
        }
        return cleanTraits;
    }
}
