package helpers;
import java.util.ArrayList;
import java.util.HashMap;

public class Creature {
    private String name; 
    private String size;
    private String type;
    private ArrayList<String> tags;
    private String alignment;
    
    private int AC;
    private String ACtype;
    private int HP;
    private String HPFormula;
    private HashMap<String, Integer> speed = new HashMap<>();

    private HashMap<String, Integer> stats = new HashMap<>();

    private HashMap<String, Integer> saves = new HashMap<>();
    private HashMap <String, Integer> skills = new HashMap<>();
    private ArrayList<String> DR = new ArrayList<>();
    private ArrayList<String> DI = new ArrayList<>();
    private ArrayList<String> CI = new ArrayList<>();
    private ArrayList<String> senses = new ArrayList<>();
    private int passive;
    private ArrayList<String> languages = new ArrayList<>();
    private int CR; 

    private ArrayList<String> traits = new ArrayList<>();
    private ArrayList<String> actions = new ArrayList<>();
    private ArrayList<String> bonusAction = new ArrayList<>();
    private ArrayList<String> reactions = new ArrayList<>();
    private ArrayList<String> LActions = new ArrayList<>();
    private ArrayList<String> mythicActions = new ArrayList<>();

    public void setHeaders(HashMap<String, String> map, ArrayList<String> tags){
        this.name = map.get("name");
        this.type = map.get("type");
        this.size = map.get("size");
        this.alignment = map.get("alignment");

        this.tags = tags;
    }

    public void setHpSection(ArrayList<String> hpSectionList, HashMap<String, Integer> speedMap){
        this.AC = Integer.parseInt(hpSectionList.get(0));
        this.ACtype = hpSectionList.get(1);
        this.HP = Integer.parseInt(hpSectionList.get(2));
        this.HPFormula = hpSectionList.get(3);

        this.speed = speedMap;
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
        int passive,
        ArrayList<String> languages,
        int CR
    ){
        this.saves = saves;
        this.skills = skills;
        this.DR = DR;
        this.DI = DI;
        this.CI = CI;
        this.senses = senses;
        this.passive = passive;
        this.languages = languages;
        this.CR = CR;
    }

    public void setTratsSection(HashMap<String, ArrayList<String>> traits){
        this.traits = traits.get("traits");
        this.actions = traits.get("actions");
        this.bonusAction = traits.get("bonus");
        this.reactions = traits.get("reactions");
        this.LActions = traits.get("LActions");
        this.mythicActions = traits.get("mythicActions");
    }

    public HashMap<String, String> getHeaders(){
        HashMap<String, String> hash = new HashMap<>();
        hash.put("name", this.name);
        hash.put("size", this.size);
        hash.put("type", this.type);
        hash.put("alignment", this.alignment);
        return hash;
    }

    public ArrayList<String> getTags(){
        return this.tags;
    }

    public void print(){
        System.out.println("name: " + this.name);
        System.out.println("size: " + this.size);
        System.out.println("type: " + this.type);
        System.out.println("tags: " + this.tags);
        System.out.println("alignment: " + this.alignment);

        System.out.println("AC: " + this.AC);
        System.out.println("AC Type: " + this.ACtype);
        System.out.println("HP: " + this.HP);
        System.out.println("HPFormula: " + this.HPFormula);
        System.out.println("Speed: " + this.speed);

        System.out.println("stats " + this.stats);

        System.out.println("saves " + this.saves);
        System.out.println("skills " + this.skills);
        System.out.println("DR " + this.DR);
        System.out.println("DI " + this.DI);
        System.out.println("CI " + this.CI);
        System.out.println("senses " + this.senses);
        System.out.println("passive " + this.passive);
        System.out.println("languages " + this.languages);
        System.out.println("CR " + this.CR);

        System.out.println("traits " + this.traits);
        System.out.println("actions " + this.actions);
        System.out.println("bonusAction " + this.bonusAction);
        System.out.println("reactions " + this.reactions);
        System.out.println("LActions " + this.LActions);
        System.out.println("mythicActions " + this.mythicActions);
    }

    public String toString() {
        return name;
    }


}
