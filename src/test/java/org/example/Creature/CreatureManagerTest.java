package org.example.Creature;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreatureManagerTest {

    @Test
    void getPointer() {
        CreatureManager manager = new CreatureManager();
        assertNull(manager.getPointer());

        manager.insertStringNode("testName", "testValue", false);
        assertNotNull(manager.getPointer());
    }

    @Test
    void movePointer() {
        CreatureManager manager = new CreatureManager();
        manager.insertStringNode("testName", "testValue", false);
        manager.movePointer();
        assertNull(manager.getPointer());
    }

    @Test
    void insertCreatedNode() {
        CreatureManager manager = new CreatureManager();
        CreatureNode node = new CreatureNode("test", "test", false);
        manager.insertCreatedNode("testNode", node);
        assertNotNull(manager.getPointer());
    }

    @Test
    void insertStringNode() {
        CreatureManager manager = new CreatureManager();
        manager.insertStringNode("testName", "testValue", false);
        assertNotNull(manager.getPointer());
    }

    @Test
    void insertNodeList() {
        CreatureManager manager = new CreatureManager();
        CreatureNode node = new CreatureNode("test", "test", false);
        manager.insertNodeList("testNode", new ArrayList<>(List.of(node)));
        assertNotNull(manager.getPointer());
    }

    @Test
    void insertFromHashMap() {
        CreatureManager manager = new CreatureManager();
        HashMap<String, String> map = new HashMap<>();
        map.put("test1", "test1");
        map.put("test2", "test2");
        manager.insertFromHashMap("name", map, true);
        assertNotNull(manager.getPointer());
    }

    @Test
    void insertFromMapListOfMaps() {
        HashMap<String, ArrayList<HashMap<String, String>>> map = new HashMap<>();
        ArrayList<HashMap<String, String>> internalList = new ArrayList<>();
        HashMap<String, String> internalMap1 = new HashMap<>();
        internalMap1.put("key1", "value1");
        HashMap<String, String> internalMap2 = new HashMap<>();
        internalMap2.put("key2", "value2");

        internalList.add(internalMap1);
        internalList.add(internalMap2);

        map.put("test", internalList);

        CreatureManager manager = new CreatureManager();
        manager.insertFromMapListOfMaps(map);

        assertNotNull(manager.getPointer());
        assertEquals(manager.getPointer().getName(), "test");
    }

    @Test
    void insertLiteralList() {
        CreatureManager manager = new CreatureManager();
        manager.insertLiteralList("name", new ArrayList<>(Arrays.asList("1", "2", "3")), false);
        assertNotNull(manager.getPointer());
        assertEquals(manager.getPointer().getListValue().size(), 3);
    }

    @Test
    void createLiteralList() {
        CreatureManager manager = new CreatureManager();
        CreatureNode node = manager.createLiteralList("test",  new ArrayList<>(Arrays.asList("1", "2", "3")), false);
        assertEquals(node.getListValue().size(), 3);
        assertEquals(node.getName(), "test");
    }
}