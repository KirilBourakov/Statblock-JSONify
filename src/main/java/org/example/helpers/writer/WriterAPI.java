package org.example.helpers.writer;

import java.io.FileWriter;
import java.io.IOException;

public class WriterAPI {
    public FileWriter writer;
    private int depth;

    public WriterAPI(String outputString){
        depth = 0;
        try {
            this.writer = new FileWriter(outputString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeName(String name){
        try {
            this.writer.write(this.formatJSONString(name) + ": ");
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeValue(String value, boolean WriteAsString){
        try {
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
    public void writeKeyValue(String key, String value, boolean WriteAsString){
        try {
            if (!WriteAsString){
                this.writer.write(this.formatJSONString(key) + ": " + value);
            } else {
                this.writer.write(this.formatJSONString(key) + ": " + this.formatJSONString(value));
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeComma(){
        try {
            this.writer.write(",");
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDepthIncreasingSection(String name, Character symbol){
        try {
            if (name == null){
                this.writer.write(symbol);
            } else {
                this.writer.write(this.formatJSONString(name) + String.format(": %c", symbol));
            }
            this.depth++;
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void endDepthIncreasingSection(Character symbol){
        try {
            this.depth--;
            this.startLine();
            this.writer.write(symbol);
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startLine(){
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
