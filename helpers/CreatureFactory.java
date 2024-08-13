package helpers;
import java.util.ArrayList;

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

    private String ReplaceNonAlphaNumeric(String input){
        input = input.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").replaceAll("  ", " ");
        input = input.strip(); 
        return input;
    }
}
