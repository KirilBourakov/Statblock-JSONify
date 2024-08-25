package helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Creature.CreatureManager;

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
    public void finish(){
        this.writer.Close();
    }
    public void setManager(CreatureManager manager){
        this.manager = manager;
    }

    public void WriteCreature(){
        if (!haveWrittenMeta){
            System.out.println("WRITING");
            this.WriteMeta();
            haveWrittenMeta = true;
        }
    }

    private void WriteMeta(){
        this.writer.StartNamedDepthIncreasingSection("_meta", '{');
        this.writer.StartNamedDepthIncreasingSection("sources", '[');

        this.writer.StartUnnamedDepthIncreasingSection('{');
        this.writer.WriteNewKeyValueLine("json", generateRandomString(5), false);
        this.writer.WriteNewKeyValueLine("abbreviation", this.inputFile, false);
        this.writer.WriteNewKeyValueLine("full", this.inputFile, false);

        this.writer.StartNamedDepthIncreasingSection("authors", '[');
        this.writer.writeValueLineNoComma("unknown", false);
        this.writer.EndDepthIncreasingSection(']');

        this.writer.StartNamedDepthIncreasingSection("convertedBy", '[');
        this.writer.writeValueLineNoComma("auto", false);
        this.writer.EndDepthIncreasingSection(']');

        this.writer.WriteNewKeyValueLine("version", "1.0", false);
        this.writer.WriteNewKeyValueLine("url", "", false);
        this.writer.WriteNewKeyValueLine("targetSchema", "1.0", false);

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
