package helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Creature.CreatureManager;
import Creature.CreatureNode;

public class NodeWriter {
    private CreatureManager manager;
    private boolean haveWrittenMeta = false;

    private WriterAPI writer;
    private String outputFile;
    private String inputFile;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public NodeWriter(String outputFile, String inputFile, CreatureManager manager){
        this.outputFile = outputFile;
        this.inputFile = inputFile;
        this.manager = manager;
        this.writer = new WriterAPI(outputFile);
    }
    public void start(){
        this.writer.StartUnnamedDepthIncreasingSection('{');
        this.WriteMeta();
    }
    public void finish(){
        this.writer.Close();
    }
    public void setManager(CreatureManager manager){
        this.manager = manager;
    }

    public void WriteCreature(){
        CreatureNode node = manager.getPointer();
        this.WalkAndWriteFromNode(node);
        
    }
    private void WalkAndWriteFromNode(CreatureNode node){
        while (node != null) {
            if (node.getName() != null) {
                this.writer.WriteName(node.getName());
            }
            node = node.getChild();
        }
    }

    private void handleObject(CreatureNode node){
        if (node.getName() == null){
            this.writer.StartUnnamedDepthIncreasingSection('{');
        } else {
            this.writer.StartNamedDepthIncreasingSection(node.getName(), '{');
        }
    }

    private void handleLiteral(CreatureNode node){
        if (node.getName() == null) {
            if (node.getChild() == null) {
                this.writer.writeValueLineNoComma(node.getValue(), node.getPrintValueAsString());
            } else {
                this.writer.writeValueLine(node.getValue(), node.getPrintValueAsString());
            }
        } else {
            if (node.getChild() == null) {
                this.writer.WriteNewKeyValueLineNoComma(node.getName(), node.getValue(), node.getPrintValueAsString());
            } else {
                this.writer.WriteNewKeyValueLine(node.getName(), node.getValue(), node.getPrintValueAsString());
            } 
        }
    }

    private void WriteMeta(){
        this.writer.StartNamedDepthIncreasingSection("_meta", '{');
        this.writer.StartNamedDepthIncreasingSection("sources", '[');

        this.writer.StartUnnamedDepthIncreasingSection('{');
        this.writer.WriteNewKeyValueLine("json", generateRandomString(5), true);
        this.writer.WriteNewKeyValueLine("abbreviation", this.inputFile, true);
        this.writer.WriteNewKeyValueLine("full", this.inputFile, true);

        this.writer.StartNamedDepthIncreasingSection("authors", '[');
        this.writer.writeValueLineNoComma("unknown", true);
        this.writer.EndDepthIncreasingSection(']');

        this.writer.StartNamedDepthIncreasingSection("convertedBy", '[');
        this.writer.writeValueLineNoComma("auto", true);
        this.writer.EndDepthIncreasingSection(']');

        this.writer.WriteNewKeyValueLine("version", "1.0", true);
        this.writer.WriteNewKeyValueLine("url", "", true);
        this.writer.WriteNewKeyValueLine("targetSchema", "1.0", true);

        this.writer.EndDepthIncreasingSectionNoComma('}');
        this.writer.EndDepthIncreasingSection(']');

        
        int time = (int) Instant.now().toEpochMilli();
        String timeValue = String.valueOf(time);
        this.writer.WriteNewKeyValueLine("dateAdded", timeValue, true);
        this.writer.WriteNewKeyValueLine("dateLastModified", timeValue, true);

        this.writer.EndDepthIncreasingSection('}');
    }

    private String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
