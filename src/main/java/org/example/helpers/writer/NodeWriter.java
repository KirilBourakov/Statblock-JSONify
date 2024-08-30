package org.example.helpers.writer;

import java.time.Instant;
import java.util.Random;

import org.example.Creature.CreatureManager;
import org.example.Creature.CreatureNode;

public class NodeWriter {
    private CreatureManager manager;
    private boolean haveWrittenOne = false;

    private WriterAPI writer;
    private String outputFile;
    private String inputFile;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public NodeWriter(String outputFile, String inputFile, CreatureManager manager){
        this.outputFile = outputFile;
        this.inputFile = inputFile;
        this.manager = manager;
    }
    public void start(){
        this.writer = new WriterAPI(this.outputFile);
        this.writer.startDepthIncreasingSection(null, '{');
        this.WriteMeta();
        this.writer.startLine();
        this.writer.startDepthIncreasingSection("monster", '[');
    }
    public void finish(){
        this.writer.endDepthIncreasingSection(']');
        this.writer.endDepthIncreasingSection('}');
        this.writer.Close();
    }
    public void setManager(CreatureManager manager){
        this.manager = manager;
    }

    public void WriteCreature(){
        CreatureNode node = manager.getPointer();

        if (haveWrittenOne){
            this.writer.writeComma();
        }

        this.writer.startLine();
        this.writer.startDepthIncreasingSection(null, '{');
        this.WalkAndWriteFromNode(node);

        this.writer.writeComma();
        this.writer.startLine();
        this.writer.writeKeyValue("source", this.inputFile, true);
        this.writer.writeComma();
        this.writer.startLine();
        this.writer.writeKeyValue("page", "0", false);
        this.writer.endDepthIncreasingSection('}');

        this.haveWrittenOne = true;
    }
    private void WalkAndWriteFromNode(CreatureNode node){
        // TODO: ADD support for writing stuff like page and source tag on the monster
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
        this.writer.writeComma();
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
