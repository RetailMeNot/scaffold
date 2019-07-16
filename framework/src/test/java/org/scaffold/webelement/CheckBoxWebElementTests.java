package org.scaffold.webelement;

import org.scaffold.BaseUnitTest;
import org.scaffold.webelements.CheckBoxWebElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CheckBoxWebElementTests extends BaseUnitTest {

    private CheckBoxWebElement checkBoxWebElement;

    @BeforeEach
    private void setupCheckBoxData() {
        checkBoxWebElement = new CheckBoxWebElement(mockElement1);
    }

    @Test
    public void testCheckFalse() {
        assertFalse(checkBoxWebElement.isSelected());
        checkBoxWebElement.check(true);
        assertTrue(checkBoxWebElement.isSelected());
        checkBoxWebElement.check(false);
        assertFalse(checkBoxWebElement.isSelected());
    }
}
