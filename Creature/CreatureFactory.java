package Creature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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

    private helpers.Parser parser = new helpers.Parser();

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

    public CreatureManager Construct(){
        System.out.println(this.headerSection);
        System.out.println(this.hpSection);
        System.out.println(this.statsSection);
        System.out.println(this.saveSection);
        System.out.println(this.traitsSection);

        this.ConstructHeaders();
        this.ConstructHpSection();
        this.ConstructStats();
        this.ConstructSaveSection();
        this.ConstructTraits();
        creature.print(null, 0);

        return this.creature;
    }

    private void ConstructHeaders(){
        ArrayList<String> finalHeaderList = new ArrayList<>();
        for (String header : headerSection) {
            finalHeaderList.add(this.parser.ReplaceNonAlphaNumeric(header).strip().toLowerCase());
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

        // insert
        creature.insertStringNode("name", this.parser.toTitleCase(finalMap.get("name")), true);
        creature.instertLiteralList("size", this.parser.getFirstLetters(finalMap.get("size")), true);
        creature.instertLiteralList("alignment", this.parser.getFirstLetters(finalMap.get("alignment")), true);

        // insert type
        if (tags.size() > 0){
            CreatureNode typeNode = new CreatureNode("type", finalMap.get("type"), true);
            CreatureNode tagNode = creature.createLiteralList("tags", tags, true);
            typeNode.setChild(tagNode);
            creature.insertCreatedNode("type", typeNode);
        } else {
            creature.insertStringNode("type", finalMap.get("type"), true);
        }
    }

    private void ConstructHpSection(){
        ArrayList<String> cleanHPSectionList = new ArrayList<>();
        for (String sectionSlice : hpSection) {
            int fi = sectionSlice.indexOf("**");
            int cutpoint = sectionSlice.indexOf("**", fi + "**".length());
            String finalHPSection = sectionSlice.substring(cutpoint+2);
            cleanHPSectionList.add(finalHPSection.strip());
        }

        // handle AC
        // TODO: handle several AC values
        String[] splitAC = this.parser.splitBeforeChar(cleanHPSectionList.get(0), "(");
        String ACvalue = this.parser.RemoveNonNumeric(splitAC[0]);
        String ACtype = this.parser.ReplaceNonAlphaNumeric(splitAC[1]);

        if (!ACtype.isEmpty()){
            CreatureNode ACvalueNode = new CreatureNode("ac", ACvalue, false);
            CreatureNode ACtypeNode = creature.createLiteralList("type", new ArrayList<>(Arrays.asList(ACtype)), true);
            ACvalueNode.setChild(ACtypeNode);
            CreatureNode AcObj = new CreatureNode(null, ACvalueNode);
            creature.insertNodeList("ac", new ArrayList<>(Arrays.asList(AcObj)));
        } else {
            creature.instertLiteralList("ac", new ArrayList<>(Arrays.asList(ACvalue)), false);
        }

        //handle HP
        String[] splitHp = this.parser.splitBeforeChar(cleanHPSectionList.get(1), "(");
        String hpValue = this.parser.RemoveNonNumeric(splitHp[0]);
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
            speedPortion = this.parser.ReplaceNonAlphaNumeric(speedPortion).strip();
            if(!speedPortion.equalsIgnoreCase("ft")){
                if (this.parser.isNumeric(speedPortion.replace("ft", ""))){
                    System.out.println("size " + speedPortion);
                    speedMap.put(speedType, this.parser.RemoveNonNumeric(speedPortion));
                } 
                //
                else if(!speedPortion.equalsIgnoreCase("ft")){
                    System.out.println("type " + speedPortion);
                    speedType = speedPortion;
                }
            }
        }

        creature.insertFromHashMap("speed", speedMap, false);
    }

    private void ConstructStats(){
        String statsStr = statsSection.get(statsSection.size()-1);
        String[] parsedStats = statsStr.split("\\)");
        ArrayList<String> finalStats = new ArrayList<>();
        for (String stat : parsedStats){
            stat = stat.replaceAll("\\|", "");
            if (stat.length() > 1) {
                stat = stat.substring(0, stat.indexOf('(')).replaceAll(" ", "");
                try {
                    finalStats.add(stat);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            }
        }
        creature.insertStringNode("str", finalStats.get(0), false);
        creature.insertStringNode("dex", finalStats.get(1), false);
        creature.insertStringNode("con", finalStats.get(2), false);
        creature.insertStringNode("int", finalStats.get(3), false);
        creature.insertStringNode("wis", finalStats.get(4), false);
        creature.insertStringNode("cha", finalStats.get(5), false);
    }

    private void ConstructSaveSection(){
        // saves
        HashMap<String, String> finalSaves = this.parser.SkillsAndSavesParser("throws", this.parser.getSaveSectionLine("saving", this.saveSection));
        if (finalSaves.size() > 0) {
            creature.insertFromHashMap("save", finalSaves, true);
        }

        // skills
        HashMap<String, String> finalSkills = this.parser.SkillsAndSavesParser("skills", this.parser.getSaveSectionLine("skill", this.saveSection));
        if (finalSkills.size() > 0) {
            creature.insertFromHashMap("skill", finalSkills, true);
        }

        // Damage resistances 
        // TODO: handle conditionals
        ArrayList<String> DR = this.parser.PunctuationSplitter("resistances", this.parser.getSaveSectionLine("resistance", this.saveSection));
        if (DR.size() > 0) {
            creature.instertLiteralList("resist", DR, true);
        }

        // Damage immunities
        // TODO: handle conditionals
        ArrayList<String> DI = this.parser.PunctuationSplitter("Immunities", this.parser.getSaveSectionLine("age im", this.saveSection));
        if (DI.size() > 0) {
            creature.instertLiteralList("immune", DI, true);
        }

        // Condition Immunities
        // TODO: handle conditionals
        ArrayList<String> CI = this.parser.PunctuationSplitter("Immunities", this.parser.getSaveSectionLine("condition", this.saveSection));
        if (CI.size() > 0) {
            creature.instertLiteralList("conditionImmune", CI, true);
        }

        // languages
        ArrayList<String> languages = this.parser.PunctuationSplitter("Languages", this.parser.getSaveSectionLine("lang", this.saveSection));
        if (languages.size() > 0) {
            creature.instertLiteralList("languages", languages, true);
        }

        // senses and passive
        
        ArrayList<String> cleanSenses = this.parser.PunctuationSplitter("Senses", this.parser.getSaveSectionLine("sense", this.saveSection));
       
        ArrayList<String> senses = new ArrayList<>(cleanSenses.stream().filter(sense -> !sense.toLowerCase().contains("passive")).collect(Collectors.toList()));
        String passive = this.parser.RemoveNonNumeric(cleanSenses.stream().filter(sense -> sense.toLowerCase().contains("passive")).findFirst().orElse("0"));
        if (senses.size() > 0) {
            creature.instertLiteralList("senses", senses, true);
        }
        creature.insertStringNode("passive", passive, false);

        // Challange Rating
        // TODO: handle special CRs (above 30, increase in lair, etc)
        String unparsedCR = this.parser.getSaveSectionLine("chall", this.saveSection);
        String CR = "0";
        if (unparsedCR.indexOf("(") > 0){
            CR = this.parser.RemoveNonNumeric(unparsedCR.substring(0, unparsedCR.indexOf("(")));
        } else {
            CR = this.parser.RemoveNonNumeric(unparsedCR);
        }
        creature.insertStringNode("cr", CR, true);
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
                        previousHeader = false;
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
                    in.add(parser.ParseATrait(trait));
                } else {
                    ArrayList<HashMap<String, String>> in = new ArrayList<>();
                    in.add(parser.ParseATrait(trait));
                    TypeMap.put(inputPoint, in);
                }
            }
        }
        creature.insertFromMapListofMaps(TypeMap);
    }
}
