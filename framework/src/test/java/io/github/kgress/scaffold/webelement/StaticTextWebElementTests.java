package io.github.kgress.scaffold.webelement;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.models.unittests.MockWebElement;
import io.github.kgress.scaffold.webelements.StaticTextWebElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticTextWebElementTests extends BaseUnitTest {

    private static String LINK_ELEMENT_TEXT = "link element";

    @Test
    public void testGetText() {
        var parent = new MockWebElement();
        var element = new MockWebElement();
        element.setText(LINK_ELEMENT_TEXT);

        parent.setElementToFind(element);
        var staticTextWebElement = new StaticTextWebElement(By.id("fake"), parent);

        assertEquals(LINK_ELEMENT_TEXT, staticTextWebElement.getText(),
                "The link's text should be 'link element");
    }

    @Test
    public void testGetTextWithoutParent() {
        var testElement = new StaticTextWebElement(By.id("fake"));
        mockWebDriver.setElementToFind(mockElement1);
        assertEquals(TEXT_NAME_1, testElement.getText(),
                "The link's text should be " + LINK_ELEMENT_TEXT);
    }
}
