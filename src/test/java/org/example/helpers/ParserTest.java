package org.example.helpers;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void splitBeforeChar() {
//      TODO: failures here
        String[] parsed = Parser.splitBeforeChar("asd_123", "_");
        assertEquals("asd", parsed[0]);
        assertEquals("123", parsed[1]);

        String[] unfoundParsed = Parser.splitBeforeChar("asd_123", "/");
        assertEquals(unfoundParsed[0], "asd_123");
        assertEquals(unfoundParsed[1], "");
    }

    @Test
    void replaceNonAlphaNumeric() {
        String s = "123==asd==123__";
        assertEquals("123 asd 123", Parser.replaceNonAlphaNumeric(s));
    }

    @Test
    void removeNonNumeric() {
        String s = "123==asd==123__";
        assertEquals("123123", Parser.removeNonNumeric(s));
    }

    @Test
    void isNumeric() {
        assertTrue(Parser.isNumeric("2313"));
        assertFalse(Parser.isNumeric("asd12323sdsafdd"));
    }

    @Test
    void punctuationSplitter() {
        String title = "title";
        String line = "Title. This would be some sort of trait or something. This is a new sentence.";
        ArrayList<String> split = Parser.punctuationSplitter(title, line);

        assertEquals(2, split.size());
    }

    @Test
    void getFirstLetters() {
        assertEquals(new ArrayList<>(Arrays.asList("A", "T", "T", "I")), Parser.getFirstLetters("a test this is"));
    }

    @Test
    void toTitleCase() {
        assertEquals("A Test This Is", Parser.toTitleCase("a test this is"));
    }

    @Test
    void handleMalformedStats() {
//        TODO: add test to handle a stat with a 30
        assertEquals(new ArrayList<>(Arrays.asList("2", "15", "8", "2", "12", "4")), Parser.handleMalformedStats("2-4152812412143"));
    }
}