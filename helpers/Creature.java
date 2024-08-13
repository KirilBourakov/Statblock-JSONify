package helpers;
import java.util.ArrayList;

public class Creature {
    private String name; 
    private String type;
    
    
    private String AC;
    private String HP;
    private String Speed;


    public void setHeaders(ArrayList<String> headerList){
        name = headerList.get(0);
        type = headerList.get(1);
    }

    public void setHpSection(ArrayList<String> hpSectionList){
        AC = hpSectionList.get(0);
        HP = hpSectionList.get(1);
        Speed = hpSectionList.get(2);
    }

    public void print(){
        System.out.println("name: " + name);
        System.out.println("type: " + type);
        System.out.println("AC: " + AC);
        System.out.println("HP: " + HP);
        System.out.println("Speed: " + Speed);
    }

}
