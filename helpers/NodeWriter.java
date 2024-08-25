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
        this.writer.startDepthIncreasingSection(null, '{');
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
            this.writer.startLine();
            if (node.getName() != null) {
                this.writer.WriteName(node.getName());
            }

            if (node.getValue() != null){
                this.writer.writeValue(node.getValue(), node.getPrintValueAsString());
            }

            if (node.getListValue() != null){
                this.writer.startDepthIncreasingSection(null, '[');
                int i = 0;
                for (CreatureNode listNode : node.getListValue() ) {
                    this.WalkAndWriteFromNode(listNode);
                    if (++i < node.getListValue().size()){
                        this.writer.writeComma();
                    }
                }
                this.writer.endDepthIncreasingSection(']');
            }

            if (node.getObjectValue() != null){
                this.writer.startDepthIncreasingSection(null, '{');
                this.WalkAndWriteFromNode(node.getObjectValue());
                this.writer.endDepthIncreasingSection('}');
            }

            if (node.getChild() != null){
                this.writer.writeComma();
            }

            node = node.getChild();
        }
    }

    private void WriteMeta(){
        this.writer.startLine();
        this.writer.startDepthIncreasingSection("_meta", '{');

        this.writer.startLine();
        this.writer.startDepthIncreasingSection("sources", '[');

        this.writer.startLine();
        this.writer.startDepthIncreasingSection(null, '{');

        this.writer.startLine();
        this.writer.writeKeyValue("json", generateRandomString(5), true);
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.writeKeyValue("abbreviation", this.inputFile, true);
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.writeKeyValue("full", this.inputFile, true);
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.startDepthIncreasingSection("authors", '[');
        this.writer.startLine();
        this.writer.writeValue("unknown", true);
        this.writer.startLine();
        this.writer.endDepthIncreasingSection(']');
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.startDepthIncreasingSection("convertedBy", '[');
        this.writer.startLine();
        this.writer.writeValue("auto", true);
        this.writer.endDepthIncreasingSection(']');
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.writeKeyValue("version", "1.0", true);
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.writeKeyValue("url", "", true);
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.writeKeyValue("targetSchema", "1.0", true);

        this.writer.endDepthIncreasingSection('}');
        this.writer.endDepthIncreasingSection(']');
        this.writer.writeComma();

        int time = (int) Instant.now().toEpochMilli();
        String timeValue = String.valueOf(time);
        this.writer.startLine();
        this.writer.writeKeyValue("dateAdded", timeValue, true);
        this.writer.writeComma();

        this.writer.startLine();
        this.writer.writeKeyValue("dateLastModified", timeValue, true);

        this.writer.endDepthIncreasingSection('}');
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
