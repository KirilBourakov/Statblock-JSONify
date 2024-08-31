package org.example.helpers.ocr;

import org.junit.jupiter.api.Test;


import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class OcrControllerTest {

    @Test
    public void SetupExitsWith0(){
        int code = OcrController.setup();
        assertEquals(0, code);
    }

    @Test
    public void ReaderReadsProperly(){
        String fileName = "images/testBlock1.png";
        File file;
        try {
            // Get the resource URL and convert it to a File object
            file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error converting resource URL to file", e);
        }
        String filePath = file.getAbsolutePath();

        OcrController.setup();
        ArrayList<String> value = OcrController.read(filePath);
        System.out.println("vale " + value);
        assertFalse(value.isEmpty());
        assertEquals(value.get(0).toLowerCase(), "test");
    }
}