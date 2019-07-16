package org.scaffold.webelement;

import org.scaffold.BaseUnitTest;
import org.scaffold.models.unittests.MockWebElement;
import org.scaffold.webelements.LinkWebElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkWebElementTests extends BaseUnitTest {

    private static String HREF_TEXT = "test href";
    private LinkWebElement linkWebElement;

    @BeforeEach
    private void setupLinkWebElementData() {
        mockElement1.setAttribute("href", HREF_TEXT);
        linkWebElement = new LinkWebElement(By.id("fake"));
    }


    @Test
    public void testGetLinkHref() {
        mockWebDriver.setElementToFind(mockElement1);
        assertEquals(HREF_TEXT, linkWebElement.getLinkHref(),
                "The link href should be " + HREF_TEXT);
    }

    @Test
    public void testGetLinkText() {
        var linkElementText = "link element";
        var parentElement = new MockWebElement();
        var childElement = new MockWebElement().text(linkElementText);

        parentElement.setElementToFind(childElement);
        var testElement = new LinkWebElement(By.id("fake"), parentElement);
        assertEquals(linkElementText, testElement.getLinkText(),
                "The link's text should be 'link element");
    }
}
