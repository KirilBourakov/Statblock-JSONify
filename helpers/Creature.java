package helpers;
import java.util.ArrayList;
import java.util.HashMap;

public class Creature {
    private String name; 
    private String type;
    
    
    private String AC;
    private String HP;
    private String Speed;

    private HashMap<String, Integer> stats = new HashMap<>();

    private HashMap<String, Integer> saves = new HashMap<>();
    private HashMap <String, Integer> skills = new HashMap<>();
    private ArrayList<String> DR = new ArrayList<>();
    private ArrayList<String> DI = new ArrayList<>();
    private ArrayList<String> CI = new ArrayList<>();
    private ArrayList<String> senses = new ArrayList<>();
    private ArrayList<String> languages = new ArrayList<>();
    private int CR; 

    private ArrayList<String> traits = new ArrayList<>();
    private ArrayList<String> actions = new ArrayList<>();
    private ArrayList<String> bonusAction = new ArrayList<>();
    private ArrayList<String> reactions = new ArrayList<>();
    private ArrayList<String> LActions = new ArrayList<>();
    private ArrayList<String> mythicActions = new ArrayList<>();

    public void setHeaders(ArrayList<String> headerList){
        this.name = headerList.get(0);
        this.type = headerList.get(1);
    }

    public void setHpSection(ArrayList<String> hpSectionList){
        this.AC = hpSectionList.get(0);
        this.HP = hpSectionList.get(1);
        this.Speed = hpSectionList.get(2);
    }

    public void setStatsSection(ArrayList<Integer> statSectionIntList){
        this.stats.put("STR", statSectionIntList.get(0));
        this.stats.put("DEX", statSectionIntList.get(1));
        this.stats.put("CON", statSectionIntList.get(2));
        this.stats.put("INT", statSectionIntList.get(3));
        this.stats.put("WIS", statSectionIntList.get(4));
        this.stats.put("CHA", statSectionIntList.get(5));
    }

    public void setSavesSection(
        HashMap<String, Integer> saves,
        HashMap <String, Integer> skills,
        ArrayList<String> DR,
        ArrayList<String> DI,
        ArrayList<String> CI,
        ArrayList<String> senses,
        ArrayList<String> languages,
        int CR
    ){
        this.saves = saves;
        this.skills = skills;
        this.DR = DR;
        this.DI = DI;
        this.CI = CI;
        this.senses = senses;
        this.languages = languages;
        this.CR = CR;
    }


    public void print(){
        System.out.println("name: " + this.name);
        System.out.println("type: " + this.type);

        System.out.println("AC: " + this.AC);
        System.out.println("HP: " + this.HP);
        System.out.println("Speed: " + this.Speed);

        System.out.println("stats " + this.stats);

        System.out.println("saves " + this.saves);
        System.out.println("skills " + this.skills);
        System.out.println("DR " + this.DR);
        System.out.println("DI " + this.DI);
        System.out.println("CI " + this.CI);
        System.out.println("senses " + this.senses);
        System.out.println("languages " + this.languages);
        System.out.println("CR " + this.CR);
    }

}
