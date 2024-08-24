package Creature;

import java.util.ArrayList;

public class CreatureManager {
    CreatureNode head = null;
    CreatureNode tail = null; 

    public void insertStringNode(String name, String value, boolean printValueAsString){
        CreatureNode node = new CreatureNode(name, value, printValueAsString);
        this.insertAtEnd(node);
    }
    public void instertLiteralList(String name, ArrayList<String> values, boolean printValueAsString){
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
        this.insertAtEnd(node);
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
