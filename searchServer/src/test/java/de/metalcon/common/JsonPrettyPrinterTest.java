package de.metalcon.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonPrettyPrinterTest {

    private void assertPrettyPrint(String json, String prettyJson) {
        assertEquals(prettyJson, JsonPrettyPrinter.prettyPrintJson(json));
    }
    
    private String in() {
        String result = "";
        for (int i = 0; i != JsonPrettyPrinter.tabWidth; ++i)
            result += " ";
        return result;
    }
    
    @Test
    public void testSimpleString() {
        assertPrettyPrint("\"foo\"", "\"foo\"");
    }

    @Test
    public void testEmptyArray() {
        assertPrettyPrint("[]",
                "[\n" +
                in() + "\n" +
                "]"
                );
    }
    
    @Test
    public void testOneElemArray() {
        assertPrettyPrint("[\"foo\"]",
                "[\n" +
                in() + "\"foo\"\n" +
                "]"
                );
    }
    
    @Test
    public void testTwoElemArray() {
        assertPrettyPrint("[\"foo\",\"bar\"]",
                "[\n" +
                in() + "\"foo\",\n" +
                in() + "\"bar\"\n" +
                "]"
                );
    }
    
    @Test
    public void testEmptyObject() {
        assertPrettyPrint("{}",
                "{\n" +
                in() + "\n" +
                "}"
                );
    }
    
    
    @Test
    public void testOneElemObject() {
        assertPrettyPrint("{\"foo\":\"bar\"}",
                "{\n" +
                in() + "\"foo\":\"bar\"\n" +
                "}"
                );
    }
    
    @Test
    public void testTwoElemObject() {
        assertPrettyPrint("{\"foo1\":\"bar1\",\"foo2\":\"bar2\"}",
                "{\n" +
                in() + "\"foo1\":\"bar1\",\n" +
                in() + "\"foo2\":\"bar2\"\n" +
                "}"
                );
    }
    
    @Test
    public void testMultipleIndent() {
        assertPrettyPrint("[[]]",
                "[\n" +
                in() + "[\n" +
                in() + in() + "\n" +
                in() + "]\n" +
                "]"
                );
        assertPrettyPrint("{{}}",
                "{\n" +
                in() + "{\n" +
                in() + in() + "\n" +
                in() + "}\n" +
                "}"
                );
    }
    
    @Test
    public void testInString() {
        assertPrettyPrint("[\"a,b\"]",
                "[\n" +
                in() + "\"a,b\"\n" +
                "]"
                );
        assertPrettyPrint("\"a,b\\\",c\"", "\"a,b\\\",c\"");
        assertPrettyPrint("[\"a\\\\\",\"b\"]",
                "[\n" +
                in() + "\"a\\\\\",\n" +
                in() + "\"b\"\n" +
                "]"
                );
    }
    
}
