package helpers;

import java.io.FileWriter;
import java.io.IOException;

public class WriterAPI {
    FileWriter writer;
    private int depth;

    public WriterAPI(String outputString){
        depth = 0;
        try {
            this.writer = new FileWriter(outputString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Close(){
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteName(String name){
        try {
            this.StartLine();
            this.writer.write(this.formatJSONString(name) + ": ");
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteNewKeyValueLine(String key, String value, boolean WriteAsString){
        try {
            this.StartLine();
            if (!WriteAsString){
                this.writer.write(this.formatJSONString(key) + ": " + value + ",");
            } else {
                this.writer.write(this.formatJSONString(key) + ": " + this.formatJSONString(value) + ",");
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteNewKeyValueLineNoComma(String key, String value, boolean WriteAsString){
        try {
            this.StartLine();
            if (!WriteAsString){
                this.writer.write(this.formatJSONString(key) + ": " + value + ",");
            } else {
                this.writer.write(this.formatJSONString(key) + ": " + this.formatJSONString(value));
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeValueLine(String value, boolean WriteAsString){
        try {
            this.StartLine();
            if (!WriteAsString){
                this.writer.write(value + ",");
            } else {
                this.writer.write(this.formatJSONString(value) + ",");
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeValueLineNoComma(String value, boolean WriteAsString){
        try {
            this.StartLine();
            if (!WriteAsString){
                this.writer.write(value);
            } else {
                this.writer.write(this.formatJSONString(value));
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartNamedDepthIncreasingSection(String name, Character symbol){
        try {
            this.StartLine();
            this.writer.write(this.formatJSONString(name) + String.format(": %c", symbol));
            this.depth++;
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void StartUnnamedDepthIncreasingSection(Character symbol){
        try {
            this.StartLine();
            this.writer.write(symbol);
            this.depth++;
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void EndDepthIncreasingSection(Character symbol){
        try {
            this.depth--;
            this.StartLine(); 
            this.writer.write(symbol + ",");
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void EndDepthIncreasingSectionNoComma(Character symbol){
        try {
            this.depth--;
            this.StartLine();
            this.writer.write(symbol);
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void StartLine(){
        try {
            this.writer.write("\n");
            for (int i = 0; i < depth; i++) {
                this.writer.write("   ");
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    private String formatJSONString(String input){
        return String.format("\"%s\"", input);
    }
}
