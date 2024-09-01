package org.example.Creature;

import java.util.ArrayList;
import java.util.Objects;

public class CreatureNode {
    String name;
    String value = null;
    boolean printValueAsString;
    CreatureNode objectValue = null;
    ArrayList<CreatureNode> listValue = null;
    CreatureNode child = null;

    public CreatureNode(String name, String value, boolean printValueAsString){
        this.name = name;
        this.value = value;
        this.printValueAsString = printValueAsString;
    }
    public CreatureNode(String name, ArrayList<CreatureNode> listValue, boolean printValueAsString){
        this.name = name;
        this.listValue = listValue;
        this.printValueAsString = printValueAsString;
    }
    public CreatureNode(String name, CreatureNode leftPointer){
        this.name = name;
        this.objectValue = leftPointer;
    }

    public void setChild(CreatureNode newChild){
        this.child = newChild;
    }
    public CreatureNode getChild(){
        return this.child;
    }
    public String getName(){
        return this.name;
    }
    public String getValue(){
        return this.value;
    }
    public CreatureNode getObjectValue(){
        return this.objectValue;
    }
    public ArrayList<CreatureNode> getListValue(){
        return this.listValue;
    }
    public boolean getPrintValueAsString(){
        return this.printValueAsString;
    }

    public boolean isValid(){
        int count = 0;
        if (this.value != null){
            count++;
        }
        if (this.objectValue != null){
            count++;
        }
        if (this.listValue != null){
            count++;
        }
        if (count > 1){
            return false;
        }
        return true;
    }

    public String getType(){
        String type = "";
        if (this.isValid()){
            if (this.value != null){
                type = "literal";
            }
            if (this.objectValue != null){
                type = "object";
            }
            if (this.listValue != null){
                type = "list";
            }
        } else {
            type = "invalid";
        }
        return type;
    }

    public String toString(){
        String type = this.getType();
        if (Objects.equals(type, "literal")){
            return this.name + ": " + this.value;
        } else if (Objects.equals(type, "object")){
            return this.name + ":";
        } else if (Objects.equals(type, "list")){
            return this.name + ": " + this.listValue;
        }

        return this.name + ": " + type + "ERROR HERE =========================";
    }
}
