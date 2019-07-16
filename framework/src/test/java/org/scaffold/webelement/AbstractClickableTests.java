package org.scaffold.webelement;

import org.scaffold.BaseUnitTest;
import org.scaffold.models.unittests.MockWebElement;
import org.scaffold.webelements.AbstractClickable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractClickableTests extends BaseUnitTest {

    private MockWebElement element = new MockWebElement();
    private AbstractClickable base;

    @BeforeEach
    private void setupAbstractClickableData() {
         base = new AbstractClickable(By.cssSelector("")) {}; // Must be set after the web driver facade is created
    }

    @Test
    public void testClick() {
        mockWebDriver.setElementToFind(element);

        assertFalse("The underlying element should not have been clicked yet", element.getIsClicked());
        base.click();
        assertTrue("The underlying element should have been clicked", element.getIsClicked());
    }

    @Test
    public void testAccountForPopups() {
        base.expectPopups();
        mockWebDriver.setElementToFind(element);

        assertFalse("The underlying element should not have been clicked yet", element.getIsClicked());
        base.click();
        assertTrue("The underlying element should have been clicked", element.getIsClicked());
    }
}
