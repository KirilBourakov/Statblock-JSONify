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


    public void print(){
        System.out.println("name: " + name);
        System.out.println("type: " + type);

        System.out.println("AC: " + AC);
        System.out.println("HP: " + HP);
        System.out.println("Speed: " + Speed);

        System.out.println("stats" + stats);
    }

}
