package helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// TODO: flatten into priority queue before writing?
public class JSONwriter {
    private ArrayList<Creature> monsters;
    private String path;
    private int depth;
    FileWriter writer;
    
    public JSONwriter(ArrayList<Creature> monsters, String path){
        this.monsters = monsters;
        this.path = path;
        this.depth = 0;

        try {
            this.writer = new FileWriter(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteCreatures(){
        try {
            for (int i = 0; i < monsters.size(); i++) {
                Creature monster = monsters.get(i);
                this.writeNewCreature();
                this.WriteHeaders(monster);
                this.WriteHPSection(monster);
                this.WriteStatsSection(monster);
                this.WriteSavesSection(monster);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private void writeNewCreature(){
        try {
            this.StartLine();
            this.writer.write("{\n");
            this.depth++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteHeaders(Creature creature){
        HashMap<String,String> hash = creature.getHeaders();
 
        this.WriteNewStringLine("name", hash.get("name"));
        this.WriteNewStringLine("source", "AUTO CREATED; SOURCE UNKNOWN");
        this.WriteNewIntLine("page", 0);

        String size = String.valueOf(hash.get("size").charAt(0));
        WriteNewStringLine("size", hash.get(size));

        this.writeType(hash, creature);

        ArrayList<String> alignmentKey = new ArrayList<>();
        for (String alignment : hash.get("alignment").split("\\s+")) {
            if (!alignment.isEmpty()) {
                alignmentKey.add(String.valueOf(alignment.charAt(0)).toUpperCase());
            }
        }
        WriteStringList("alignment", alignmentKey);
        WriteOnlyLiteral(",");

    }

    private void WriteHPSection(Creature creature){
        HashMap<String, Integer> values = creature.getHPSectionValues();
        HashMap<String, String> types = creature.getHPSectionTypes();
        HashMap<String, Integer> speed = creature.getSpeed();

        // Write AC
        this.WriteNewLitteralLine("ac", ": [");
        this.depth++;
        if (types.get("AC").length() > 0){
            this.WriteOnlyLiteralLine("{");
            this.depth++;
            this.WriteNewIntLine("ac", values.get("AC"));

            this.WriteNewLitteralLine("from", ": [");
            this.depth++;
            this.WriteOnlyStringLine(types.get("AC"));
            this.depth--;
            this.WriteOnlyLiteralLine("]");

            this.depth--;
            this.WriteOnlyLiteralLine("}");
        } else {
            this.depth++;
            this.WriteOnlyIntLine(values.get("AC"));
            this.depth--;
        }
        this.depth--;
        this.WriteOnlyLiteralLine("],");

        // Write HP
        this.WriteNewLitteralLine("hp", ": {");
        this.depth++;
        this.WriteNewIntLine("average", values.get("HP"));
        this.WriteNewStringLineWithoutCommaEnd("formula", types.get("HP"));
        this.depth--;
        this.WriteOnlyLiteralLine("},");

        // write Speed
        this.WriteNewLitteralLine("speed", ": {");
        this.depth++;

        int size = speed.size();
        int i = 0;
        for (Map.Entry<String, Integer> entry : speed.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (++i == size) {
                this.WriteNewIntLineWithoutCommaEnd(key,value);
            } else {
                this.WriteNewIntLine(key, value);
            }
        }   
        this.depth--;
        this.WriteOnlyLiteralLine("},");

    }
    
    private void WriteStatsSection(Creature creature){
        for (Map.Entry<String, Integer> entry : creature.getStats().entrySet()) {
            this.WriteNewIntLine(entry.getKey(), entry.getValue());
        }
    }

    private void WriteSavesSection(Creature creature){
        this.WriteSkillsAndSaves("save", creature.getSaves());
        this.WriteSkillsAndSaves("skill", creature.getSkills());
        this.WriteStringListCommaEnd("senses", creature.getSenses());
        this.WriteNewIntLine("passive", creature.getPassive());

        HashMap<String, ArrayList<String>> getResandImMap = creature.getResandIm();
        this.WriteStringListCommaEnd("resist", getResandImMap.get("DR"));
        this.WriteStringListCommaEnd("immune", getResandImMap.get("DI"));
        this.WriteStringListCommaEnd("conditionImmune", getResandImMap.get("CI"));

        this.WriteStringListCommaEnd("languages", creature.getLanguages());
        this.WriteNewStringLine("cr", String.valueOf(creature.getCR()));
    }

    private void WriteSkillsAndSaves(String title, HashMap<String, String> contents){
        this.WriteNewLitteralLine(title, ": {");
        this.depth++;

        int i = 0;
        int size = contents.size();
        for (Map.Entry<String, String> entry : contents.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (++i == size) {
                this.WriteNewStringLineWithoutCommaEnd(key.toLowerCase(), value);
            } else {
                this.WriteNewStringLine(key.toLowerCase(), value);
            }
        }   
        this.depth--;
        this.WriteOnlyLiteralLine("},");
    }

    private void writeType(HashMap<String,String> hash, Creature creature){
        this.WriteNewLitteralLine("type", ": {");
        this.depth++;

        this.WriteNewStringLine("type", hash.get("type"));

        ArrayList<String> tags = creature.getTags();
        if (tags.size() > 0){
            this.WriteStringList("tags", tags);
        }
        this.depth--;
        this.WriteOnlyLiteralLine("},");
    }

    private void WriteStringList(String key, ArrayList<String> values){
        this.WriteNewLitteralLine(key, ": [");
        this.depth++;
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            this.WriteOnlyStringLine(value);
            if (i+1 != values.size()){
                try {
                    this.writer.write(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        this.depth--;
        this.WriteOnlyLiteralLine("]");
    }

    private void WriteStringListCommaEnd(String key, ArrayList<String> values){
        this.WriteNewLitteralLine(key, ": [");
        this.depth++;
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            this.WriteOnlyStringLine(value);
            if (i+1 != values.size()){
                try {
                    this.writer.write(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        this.depth--;
        this.WriteOnlyLiteralLine("],");
    }

    private void WriteNewStringLineWithoutCommaEnd(String key, String value){
        try {
            this.StartLine();
            this.writer.write(formatJSONString(key) + ": " + formatJSONString(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteNewStringLine(String key, String value){
        try {
            this.StartLine();
            this.writer.write(formatForJSONString(key, value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteNewIntLine(String key, Integer value){
        try {
            this.StartLine();
            this.writer.write(formatForJSONInt(key, value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteNewIntLineWithoutCommaEnd(String key, Integer value){
        try {
            this.StartLine();
            this.writer.write(formatJSONString(key) + ": " + value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteOnlyIntLine(Integer value){
        try {
            this.StartLine();
            this.writer.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void WriteNewLitteralLine(String key, String value){
        try {
            this.StartLine();
            this.writer.write(formatJSONString(key) + value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void WriteOnlyStringLine(String input){
        try {
            this.StartLine();
            this.writer.write(formatJSONString(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteOnlyLiteralLine(String input){
        try {
            this.StartLine();
            this.writer.write(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteOnlyLiteral(String input){
        try {
            this.writer.write(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String formatForJSONString(String key, String value){
        return (formatJSONString(key) + ": " + formatJSONString(value) + ",");
    }
    private String formatForJSONInt(String key, Integer value){
        return (formatJSONString(key) + ": " + value + ",");
    }
    private void StartLine(){
        try {
            this.writer.write("\n");
            for (int i = 0; i < depth; i++) {
                this.writer.write("   ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


    private String formatJSONString(String input){
        return String.format("\"%s\"", input);
    }
}
