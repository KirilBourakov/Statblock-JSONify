package org.example.Creature;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CreatureNodeTest {
    @Test
    void setAndGetChild() {
        CreatureNode valueNode = new CreatureNode("test", "test", true);
        CreatureNode testerNode = new CreatureNode("test", "test", true);

        valueNode.setChild(testerNode);
        assertEquals(valueNode.getChild(), testerNode);

        valueNode.setChild(null);
        assertNull(valueNode.getChild());
    }

    @Test
    void getName() {
        CreatureNode valueNode = new CreatureNode("name", "value", true);
        assertEquals(valueNode.getName(), "name");
    }

    @Test
    void getValue() {
        CreatureNode valueNode = new CreatureNode("name", "value", true);
        assertEquals(valueNode.getValue(), "value");
    }

    @Test
    void getObjectValue() {
        CreatureNode valueNode = new CreatureNode("name", "value", true);
        CreatureNode nodeNode = new CreatureNode("name", valueNode);
        assertEquals(nodeNode.getObjectValue(), valueNode);
    }

    @Test
    void getListValue() {
        CreatureNode valueNode = new CreatureNode("name", "value", true);
        CreatureNode nodeNode = new CreatureNode("name", new ArrayList<>(List.of(valueNode)), true);
        assertEquals(nodeNode.getListValue(), new ArrayList<>(List.of(valueNode)));
    }

    @Test
    void getPrintValueAsString() {
        CreatureNode falseNode = new CreatureNode("name", "value", false);
        CreatureNode trueNode = new CreatureNode("name", "value", true);

        assertFalse(falseNode.getPrintValueAsString());
        assertTrue(trueNode.getPrintValueAsString());
    }

    @Test
    void isValid() {
        CreatureNode node = new CreatureNode("name", "value", false);
        assertTrue(node.isValid());
    }

    @Test
    void getType() {
        CreatureNode node = new CreatureNode("name", "value", false);
        CreatureNode listNode = new CreatureNode("name", new ArrayList<>(List.of(node)), true);
        CreatureNode nodeNode = new CreatureNode("name", node);

        assertEquals(node.getType(), "literal");
        assertEquals(listNode.getType(), "list");
        assertEquals(nodeNode.getType(), "object");
    }
}