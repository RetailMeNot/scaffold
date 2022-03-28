package io.github.kgress.scaffold.webelement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.MockBaseWebElement;
import io.github.kgress.scaffold.SharedTestVariables;
import io.github.kgress.scaffold.util.AutomationUtils;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class BaseWebElementTests extends BaseUnitTest {

    // For testing non findElement or findElements methods
    private final TestBaseWebElement elementByCssSelector = new TestBaseWebElement(SharedTestVariables.CSS_SELECTOR1);
    private final TestBaseWebElement elementByClass =
            new TestBaseWebElement(By.className(SharedTestVariables.CLASS_NAME));

    // For testing findElement or findElements methods
    private final MockBaseWebElement parentBaseWebElementByCss = new MockBaseWebElement(
            SharedTestVariables.MOCK_PARENT_ELEMENT_SELECTOR);
    private final MockBaseWebElement parentBaseWebElementByClass = new MockBaseWebElement(
            By.className(SharedTestVariables.CLASS_NAME));
    private final MockBaseWebElement parentBaseWebElementBy = new MockBaseWebElement(
            By.cssSelector(SharedTestVariables.MOCK_PARENT_ELEMENT_SELECTOR));
    private final MockBaseWebElement parentBaseWebElementByWithParent = new MockBaseWebElement(
            By.cssSelector(SharedTestVariables.MOCK_PARENT_ELEMENT_SELECTOR),
            By.cssSelector(SharedTestVariables.MOCK_CHILD_ELEMENT_SELECTOR));
    private final By expectedByCssSelector = By.cssSelector(SharedTestVariables.MOCK_PARENT_ELEMENT_SELECTOR);
    private final By expectedByClassName = By.className(SharedTestVariables.CLASS_NAME);
    private final By expectedCombinedBy = By.cssSelector(SharedTestVariables.EXPECTED_COMBINED_SELECTOR);
    private final By expectedBy = By.cssSelector(SharedTestVariables.CSS_SELECTOR1);
    private MockBaseWebElement foundElement;

    @Test
    public void testIsEnabled_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.isEnabled()).thenReturn(true);
        assertTrue(elementByCssSelector.isEnabled());
    }

    @Test
    public void testIsDisabled_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.isEnabled()).thenReturn(false);
        assertFalse(elementByCssSelector.isEnabled());
    }

    @Test
    public void testIsDisplayed_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.isDisplayed()).thenReturn(true);
        assertTrue(elementByCssSelector.isDisplayed());
    }

    @Test
    public void testIsNotDisplayed_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.isDisplayed()).thenReturn(false);
        assertFalse(elementByCssSelector.isDisplayed());
    }

    @Test
    public void testIsActive_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.ACTIVE_CLASS_NAME);
        assertTrue(elementByCssSelector.isActive());
    }

    @Test
    public void testIsNotActive_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertFalse(elementByCssSelector.isActive());
    }

    @Test
    public void testHasClass_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertTrue(elementByCssSelector.hasClass(SharedTestVariables.CLASS_NAME));
    }

    @Test
    public void testDoesNotHaveClass_byCss() {
        setBaseWhen(elementByCssSelector);
        var notExpectingText = "NOT HERE <.< >.>";
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertFalse(elementByCssSelector.hasClass(notExpectingText));
    }

    @Test
    public void testGetAttribute_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertEquals(SharedTestVariables.CLASS_NAME,
                elementByCssSelector.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE));
    }

    @Test
    public void testGetText_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getText()).thenReturn(SharedTestVariables.TEXT_1);
        assertEquals(SharedTestVariables.TEXT_1, elementByCssSelector.getText());
    }

    @Test
    public void testGetTagName_byCss() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getTagName()).thenReturn(SharedTestVariables.TAG_NAME_1);
        assertEquals(SharedTestVariables.TAG_NAME_1, elementByCssSelector.getTagName());
    }

    @Test
    public void testGetLocation_byCss() {
        setBaseWhen(elementByCssSelector);
        var testPoint = new Point(1, 1);
        when(mockRawWebElement.getLocation()).thenReturn(testPoint);
        assertEquals(testPoint, elementByCssSelector.getLocation());
    }

    @Test
    public void testGetSize_byCss() {
        setBaseWhen(elementByCssSelector);
        var testDimension = new Dimension(1,1);
        when(mockRawWebElement.getSize()).thenReturn(testDimension);
        assertEquals(testDimension, elementByCssSelector.getSize());
    }

    @Test
    public void testGetRect_byCss() {
        setBaseWhen(elementByCssSelector);
        var testRectangle = new Rectangle(1, 1, 1, 1);
        when(mockRawWebElement.getRect()).thenReturn(testRectangle);
        assertEquals(testRectangle, elementByCssSelector.getRect());
    }

    @Test
    public void testGetCssValue_byCss() {
        setBaseWhen(elementByCssSelector);
        var testCssProperty = "testProperty";
        var expectedTestCssValue = "Dagobah";
        when(mockRawWebElement.getCssValue(testCssProperty)).thenReturn(expectedTestCssValue);
        assertEquals(expectedTestCssValue, elementByCssSelector.getCssValue(testCssProperty));
    }

    @Test
    public void testGetRawWebElement_byCss_success() {
        setBaseWhen(elementByCssSelector);
        assertEquals(mockRawWebElement, elementByCssSelector.getRawWebElement());
    }

    @Test
    public void testGetRawWebElement_byCss_fail() {
        setBaseWhen(elementByCssSelector);
        when(elementByCssSelector.getRawWebElement()).thenThrow(TimeoutException.class);
        assertThrows(TimeoutException.class, elementByCssSelector::getRawWebElement);
    }

    @Test
    public void testGetRawParentWebElement_byCss_success() {
        setBaseWhen(elementByCssSelector);
        setWhenGetRawParentElementSucceed();
        assertEquals(mockParentRawWebElement, elementByCssSelector.getRawParentWebElement());
    }

    @Test
    public void testGetRawParentWebElement_byCss_fail() {
        setBaseWhen(elementByCssSelector);
        setWhenGetRawParentElementFail();
        assertThrows(TimeoutException.class, elementByCssSelector::getRawParentWebElement);
    }

    @Test
    public void testScrollIntoView_byCss_success() {
        setBaseWhen(elementByCssSelector);
        setWhenScrollIntoViewSucceed();
        assertEquals(mockRawWebElement, elementByCssSelector.scrollIntoView());
    }

    @Test
    public void testScrollIntoView_byCss_fail() {
        setBaseWhen(elementByCssSelector);
        setWhenScrollIntoViewFail();
        assertThrows(TimeoutException.class, elementByCssSelector::scrollIntoView);
    }

    @Test
    public void testIsEnabled_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.isEnabled()).thenReturn(true);
        assertTrue(elementByClass.isEnabled());
    }

    @Test
    public void testIsDisabled_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.isEnabled()).thenReturn(false);
        assertFalse(elementByClass.isEnabled());
    }

    @Test
    public void testIsDisplayed_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.isDisplayed()).thenReturn(true);
        assertTrue(elementByClass.isDisplayed());
    }

    @Test
    public void testIsNotDisplayed_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.isDisplayed()).thenReturn(false);
        assertFalse(elementByClass.isDisplayed());
    }

    @Test
    public void testIsActive_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.ACTIVE_CLASS_NAME);
        assertTrue(elementByClass.isActive());
    }

    @Test
    public void testIsNotActive_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertFalse(elementByClass.isActive());
    }

    @Test
    public void testHasClass_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertTrue(elementByClass.hasClass(SharedTestVariables.CLASS_NAME));
    }

    @Test
    public void testDoesNotHaveClass_byClass() {
        setBaseWhen(elementByClass);
        var notExpectingText = "NOT HERE <.< >.>";
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertFalse(elementByClass.hasClass(notExpectingText));
    }

    @Test
    public void testGetAttribute_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertEquals(SharedTestVariables.CLASS_NAME,
                elementByClass.getAttribute(SharedTestVariables.CLASS_ATTRIBUTE));
    }

    @Test
    public void testGetText_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.getText()).thenReturn(SharedTestVariables.TEXT_1);
        assertEquals(SharedTestVariables.TEXT_1, elementByClass.getText());
    }

    @Test
    public void testGetTagName_byClass() {
        setBaseWhen(elementByClass);
        when(mockRawWebElement.getTagName()).thenReturn(SharedTestVariables.TAG_NAME_1);
        assertEquals(SharedTestVariables.TAG_NAME_1, elementByClass.getTagName());
    }

    @Test
    public void testGetLocation_byClass() {
        setBaseWhen(elementByClass);
        var testPoint = new Point(1, 1);
        when(mockRawWebElement.getLocation()).thenReturn(testPoint);
        assertEquals(testPoint, elementByClass.getLocation());
    }

    @Test
    public void testGetSize_byClass() {
        setBaseWhen(elementByClass);
        var testDimension = new Dimension(1,1);
        when(mockRawWebElement.getSize()).thenReturn(testDimension);
        assertEquals(testDimension, elementByClass.getSize());
    }

    @Test
    public void testGetRect_byClass() {
        setBaseWhen(elementByClass);
        var testRectangle = new Rectangle(1, 1, 1, 1);
        when(mockRawWebElement.getRect()).thenReturn(testRectangle);
        assertEquals(testRectangle, elementByClass.getRect());
    }

    @Test
    public void testGetCssValue_byClass() {
        setBaseWhen(elementByClass);
        var testCssProperty = "testProperty";
        var expectedTestCssValue = "Dagobah";
        when(mockRawWebElement.getCssValue(testCssProperty)).thenReturn(expectedTestCssValue);
        assertEquals(expectedTestCssValue, elementByClass.getCssValue(testCssProperty));
    }

    @Test
    public void testGetRawWebElement_byClass_success() {
        setBaseWhen(elementByClass);
        assertEquals(mockRawWebElement, elementByClass.getRawWebElement());
    }

    @Test
    public void testGetRawWebElement_byClass_fail() {
        setBaseWhen(elementByClass);
        when(elementByClass.getRawWebElement()).thenThrow(TimeoutException.class);
        assertThrows(TimeoutException.class, elementByClass::getRawWebElement);
    }

    @Test
    public void testGetRawParentWebElement_byClass_success() {
        setBaseWhen(elementByClass);
        setWhenGetRawParentElementSucceed();
        assertEquals(mockParentRawWebElement, elementByClass.getRawParentWebElement());
    }

    @Test
    public void testGetRawParentWebElement_byClass_fail() {
        setBaseWhen(elementByClass);
        setWhenGetRawParentElementFail();
        assertThrows(TimeoutException.class, elementByClass::getRawParentWebElement);
    }

    @Test
    public void testScrollIntoView_byClass_success() {
        setBaseWhen(elementByClass);
        setWhenScrollIntoViewSucceed();
        assertEquals(mockRawWebElement, elementByClass.scrollIntoView());
    }

    @Test
    public void testScrollIntoView_byClass_fail() {
        setBaseWhen(elementByClass);
        setWhenScrollIntoViewFail();
        assertThrows(TimeoutException.class, elementByClass::scrollIntoView);
    }

    // These tests below all use MockBaseWebElement and NOT the TestBaseWebElement from base unit test
    @Test
    public void testFindBaseWebElement_css_combinedBy() {
        foundElement = parentBaseWebElementByCss
                .findElement(MockBaseWebElement.class, SharedTestVariables.CSS_SELECTOR1);
        assertEquals(expectedCombinedBy, foundElement.getBy());
    }

    @Test
    public void testExceptionHandledIsDisplayed(){
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.isDisplayed()).thenThrow(new TimeoutException());
        assertDoesNotThrow(elementByCssSelector::isDisplayed);
        assertFalse(elementByCssSelector.isDisplayed());
    }

    @Test
    public void testExceptionHandledIsEnabled(){
        setBaseWhen(elementByCssSelector);
        when(elementByCssSelector.getRawWebElement()).thenThrow(new TimeoutException());
        assertDoesNotThrow(elementByCssSelector::isEnabled);
        assertFalse(elementByCssSelector.isEnabled());
    }

    @Test
    public void testExceptionHandledIsActive(){
        setBaseWhen(elementByCssSelector);
        when(elementByCssSelector.getRawWebElement()).thenThrow(new TimeoutException());
        assertDoesNotThrow(elementByCssSelector::isActive);
        assertFalse(elementByCssSelector.isActive());
    }

    @Test
    public void testExceptionHandledHasClass(){
        setBaseWhen(elementByCssSelector);
        when(elementByCssSelector.getRawWebElement()).thenThrow(new TimeoutException());
        assertDoesNotThrow(() -> elementByCssSelector.hasClass("class"));
        assertFalse(elementByCssSelector.hasClass("class"));
    }

  // these tests use both TestBaseWebElement & MockBaseWebElement

    @Test
    public void testBaseWebElement_findElements_by_xPath() {
        final By parentBySelector = By.xpath("//div[@class='reservation-container']");
        final By rootChildBySelector = By.xpath("//div[@class='reservation-card-container']");
        final WebElement reservationCard_A = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        when(webElement.findElements(rootChildBySelector)).thenReturn(
            List.of(reservationCard_A, reservationCard_A, reservationCard_A)
        );
        TestBaseWebElement parentElement = new TestBaseWebElement(parentBySelector);
        when(parentElement.getRawWebElement()).thenReturn(webElement);
        assertThat(parentElement).isNotNull();
        // now assert Order of child elements is what we expect...
        List<MockBaseWebElement> reservationCards1 =
            parentElement.findElements(MockBaseWebElement.class, rootChildBySelector);
        assertThat(reservationCards1).isNotNull();
        assertThat(reservationCards1.size()).isEqualTo(3);
        final var reservationCards = reservationCards1;
        IntStream.range(0, reservationCards.size()).forEach((i) -> {
            switch (i) {
                case 0:
                    final String aByString = AutomationUtils.getUnderlyingLocatorByString(
                        reservationCards.get(i).getBy());
                    assertThat(aByString).isEqualTo(
                        "//div[@class='reservation-card-container'][1]");
                    break;
                case 1:
                    final String bByString = AutomationUtils.getUnderlyingLocatorByString(
                        reservationCards.get(i).getBy());
                    assertThat(bByString).isEqualTo(
                        "//div[@class='reservation-card-container'][2]");
                    break;
                case 2:
                    final String cByString = AutomationUtils.getUnderlyingLocatorByString(
                        reservationCards.get(i).getBy());
                    assertThat(cByString).isEqualTo(
                        "//div[@class='reservation-card-container'][3]");
                    break;
            }
        });
    }

    @Test
    public void testBaseWebElement_findElements_by_css() {
        final By parentBySelector = By.cssSelector("div.reservation-container");
        final By rootChildBySelector = By.cssSelector("div.reservation-card-container");
        final WebElement reservationCard_A = mock(WebElement.class);
        // this mock webelement instance is necessary to distinguish from reservationCard, since the
        // findElements method does an Object.equals to see if the instance is in the filtered list
        // of requested items.
        final WebElement rogueDiv = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        when(reservationCard_A.getTagName()).thenReturn("div");
        when(reservationCard_A.findElement(any(By.ByXPath.class))).thenReturn(webElement);
        // order here is important, to validate that rogueDiv causes the index of the desired elements
        // to be nonsequential
        when(webElement.findElements(any(By.ByTagName.class))).thenReturn(
            List.of(reservationCard_A, rogueDiv, reservationCard_A, reservationCard_A)
        );
        when(webElement.findElements(rootChildBySelector)).thenReturn(
            List.of(reservationCard_A, reservationCard_A, reservationCard_A)
        );
        TestBaseWebElement parentElement = new TestBaseWebElement(parentBySelector);
        when(parentElement.getRawWebElement()).thenReturn(webElement);
        assertThat(parentElement).isNotNull();
        // now assert Order of child elements is what we expect...
        List<MockBaseWebElement> reservationCards1 =
            parentElement.findElements(MockBaseWebElement.class, rootChildBySelector);
        assertThat(reservationCards1).isNotNull();
        assertThat(reservationCards1.size()).isEqualTo(3);
        final var reservationCards = reservationCards1;
        IntStream.range(0, reservationCards.size()).forEach((i) -> {
            switch (i) {
                case 0:
                    final String aByString = AutomationUtils.getUnderlyingLocatorByString(
                        reservationCards.get(i).getBy());
                    assertThat(aByString).isEqualTo(
                        "div.reservation-container div.reservation-card-container:nth-child(1)");
                    break;
                case 1:
                    final String bByString = AutomationUtils.getUnderlyingLocatorByString(
                        reservationCards.get(i).getBy());
                    assertThat(bByString).isEqualTo(
                        "div.reservation-container div.reservation-card-container:nth-child(3)");
                    break;
                case 2:
                    final String cByString = AutomationUtils.getUnderlyingLocatorByString(
                        reservationCards.get(i).getBy());
                    assertThat(cByString).isEqualTo(
                        "div.reservation-container div.reservation-card-container:nth-child(4)");
                    break;
            }
        });
    }

    @Test
    public void testBaseWebElement_findElements_returnsEmptyList() {
        final By parentBySelector = By.cssSelector("div.reservation-container");
        final By rootChildBySelector = By.cssSelector("li");
        final WebElement webElement = mock(WebElement.class);
        // order here is important, to validate that rogueDiv causes the index of the desired elements
        // to be nonsequential
        when(webElement.findElements(rootChildBySelector)).thenReturn(
            List.of()
        );
        TestBaseWebElement parentElement = new TestBaseWebElement(parentBySelector);
        when(parentElement.getRawWebElement()).thenReturn(webElement);
        assertThat(parentElement).isNotNull();
        // now assert Order of child elements is what we expect...
        List<MockBaseWebElement> reservationCards =
            parentElement.findElements(MockBaseWebElement.class, rootChildBySelector);
        assertThat(reservationCards).isNotNull();
        assertThat(reservationCards).isEmpty();
    }
}
