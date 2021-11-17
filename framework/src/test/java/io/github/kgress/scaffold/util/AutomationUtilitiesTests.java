package io.github.kgress.scaffold.util;

import io.github.kgress.scaffold.BaseUnitTest;
import org.junit.jupiter.api.Test;

import static io.github.kgress.scaffold.util.AutomationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomationUtilitiesTests extends BaseUnitTest {

    @Test
    public void testGetTextFromHTML() {
        var html = "<head>Hello!</head><body><h1>This is the body</body>";
        assertEquals("<h1>This is the ", getTextFromHTML(html, "<body>", "body"),
                "The Text from within the designated boundaries should be '<h1>This is the '");
    }

    @Test
    public void testGetURLEncodedString() {
        assertEquals("+", getURLEncodedString(" "),
                "The URL-encoded character ' ' should come back as '+'");
    }

    @Test
    public void testGetTextFromHTMLNoStartsWith() {
        var html = "<head>Hello!</head><body><h1>This is the body</body>";
        assertTrue(getTextFromHTML(html, "not there", "body").isEmpty(),
                "Passing a startsWith that isn't there should return an empty string");
    }

    @Test
    public void testGetTextFromHTMLNoEndsWith() {
        var html = "<head>Hello!</head><body><h1>This is the body</body>";
        assertTrue(getTextFromHTML(html, "<body>", "not there").isEmpty(),
                "Passing an endsWith that isn't there should return an empty string");
    }

    @Test
    public void testGetUniqueString() {
        assertEquals(32, getUniqueString().length(), "The uniqueString should be 32 chars long");
    }

    @Test
    public void testGetUniqueStringCharLength() {
        assertEquals(5, getUniqueString(5).length(),
                "The uniqueString should be 5 chars");
        assertEquals(15, getUniqueString(15).length(),
                "The uniqueString should be 15 chars");
        assertEquals(32, getUniqueString(32).length(),
                "The uniqueString should be 32 chars");
        assertEquals(56, getUniqueString(56).length(),
                "The uniqueString should be 56 chars");
        assertEquals(972, getUniqueString(972).length(),
                "The uniqueString should be 972 chars");
        assertEquals(32, getUniqueString(0).length(),
                "The call to uniqueString(0) should be 32 chars");
        assertEquals(32, getUniqueString(-32767).length(),
                "The call to uniqueString(-32767) should be 32 chars");
    }
}
