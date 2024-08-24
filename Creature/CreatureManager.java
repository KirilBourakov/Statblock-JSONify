package Creature;

import java.util.ArrayList;
import java.util.HashMap;

public class CreatureManager {
    CreatureNode head = null;
    CreatureNode tail = null; 

    public void insertCreatedNode(String name, CreatureNode node){
        CreatureNode owner = new CreatureNode(name, node);
        this.insertAtEnd(owner);
    }

    public void insertStringNode(String name, String value, boolean printValueAsString){
        CreatureNode node = new CreatureNode(name, value, printValueAsString);
        this.insertAtEnd(node);
    }

    public void insertNodeList(String name, ArrayList<CreatureNode> values){
        CreatureNode node = new CreatureNode(name, values, false);
        this.insertAtEnd(node);
    }

    public void insertFromHashMap(String name, HashMap<String,String> values, boolean printValueAsString){
        CreatureNode current = null;
        CreatureNode head = current;
        for (String key : values.keySet()) {
            if (current == null){
                current = new CreatureNode(key, values.get(key), printValueAsString);
                head = current;
            }
            current.child = new CreatureNode(key, values.get(key), printValueAsString);
            current = current.child;
        };
        CreatureNode node = new CreatureNode(name, head);
        this.insertAtEnd(node);
    }

    public void instertLiteralList(String name, ArrayList<String> values, boolean printValueAsString){
        CreatureNode node = this.createLiteralList(name, values, printValueAsString);
        this.insertAtEnd(node);
    }
    public CreatureNode createLiteralList(String name, ArrayList<String> values, boolean printValueAsString){
        ArrayList<CreatureNode> finalList = new ArrayList<>(); 

        CreatureNode last = null;
        for (String value : values) {
            CreatureNode tmpNode = new CreatureNode(null, value, printValueAsString);
            if (last != null){
                last.setChild(tmpNode);
            }
            finalList.add(tmpNode);
            last = tmpNode;
        }
        CreatureNode node = new CreatureNode(name, finalList, printValueAsString);
        return node;
    }

    public void insertAtEnd(CreatureNode node){
        if (head == null){
            this.head = node;
        }
        if (tail == null){
            this.tail = node;
        } else {
            this.tail.setChild(node);
            this.tail = node;
        }
    }

    public void print(){
        System.out.println("----------------");
        CreatureNode current = this.head;
        while (current != null ) {
            System.out.println(current);
            current = current.child;
        }   
    }
}
