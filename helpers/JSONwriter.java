package helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    }

    private void writeType(HashMap<String,String> hash, Creature creature){
        WriteNewLitteralLine("type", ": {");
        this.depth++;

        WriteNewStringLine("type", hash.get("type"));

        ArrayList<String> tags = creature.getTags();
        if (tags.size() > 0){
            WriteStringList("tags", tags);
        }
        this.depth--;
        WriteOnlyLiteral("},");
    }

    private void WriteStringList(String key, ArrayList<String> values){
        WriteNewLitteralLine(key, ": [");
        this.depth++;
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            this.writeOnlyString(value);
            if (i+1 != values.size()){
                try {
                    this.writer.write(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        this.depth--;
        WriteOnlyLiteral("]");
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

    private void WriteNewLitteralLine(String key, String value){
        try {
            this.StartLine();
            this.writer.write(formatJSONString(key) + value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void writeOnlyString(String input){
        try {
            this.StartLine();
            this.writer.write(formatJSONString(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void WriteOnlyLiteral(String input){
        try {
            this.StartLine();
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
