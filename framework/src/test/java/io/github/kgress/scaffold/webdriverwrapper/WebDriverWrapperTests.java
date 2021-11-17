package io.github.kgress.scaffold.webdriverwrapper;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.SharedTestVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WebDriverWrapperTests extends BaseUnitTest {

    public final static String TEST_CSS_SELECTOR2 = ".element2";
    public final static String TEST_URL = "https://someurl.com";
    private TestWebDriverWrapper testWebDriverWrapper;

    @Mock
    private WebElement mockWebElement1;

    @Mock
    private WebElement mockWebElement2;

    @BeforeEach
    public void setup() {
        testWebDriverWrapper = createTestWebDriverWrapper();
    }

    @Test
    public void testFindElement() {
        when(mockWebElement1.getText()).thenReturn(SharedTestVariables.TEXT_1);
        when(mockBaseWebDriver.findElement(By.cssSelector(SharedTestVariables.CSS_SELECTOR1)))
                .thenReturn(mockWebElement1);
        assertEquals(SharedTestVariables.TEXT_1,
                testWebDriverWrapper.findElement(By.cssSelector(SharedTestVariables.CSS_SELECTOR1)).getText());
    }

    @Test
    public void testFindElements() {
        when(mockWebElement1.getText()).thenReturn(SharedTestVariables.TEXT_1);
        when(mockWebElement2.getText()).thenReturn(SharedTestVariables.TEXT_2);
        List<WebElement> elementList = new ArrayList<>();
        elementList.add(mockWebElement1);
        elementList.add(mockWebElement2);
        when(mockBaseWebDriver.findElements(By.cssSelector(SharedTestVariables.CSS_SELECTOR1)))
                .thenReturn(elementList);
        when(mockBaseWebDriver.findElements(By.cssSelector(TEST_CSS_SELECTOR2))).thenReturn(elementList);
        assertEquals(SharedTestVariables.TEXT_1, testWebDriverWrapper.
                findElements(By.cssSelector(SharedTestVariables.CSS_SELECTOR1))
                .get(0)
                .getText());
        assertEquals(SharedTestVariables.TEXT_2, testWebDriverWrapper
                .findElements(By.cssSelector(TEST_CSS_SELECTOR2))
                .get(1)
                .getText());
    }

    @Test
    public void testGet() {
        mockWebDriverWrapper.get(TEST_URL);
        verify(mockWebDriverWrapper, times(1)).get(TEST_URL);
    }
    @Test
    public void testCurrentUrl() {
        when(mockBaseWebDriver.getCurrentUrl()).thenReturn(TEST_URL);
        assertEquals(TEST_URL, testWebDriverWrapper.getCurrentUrl());
    }

    @Test
    public void testFindElementDoesntExist() {
        when(mockBaseWebDriver.findElement(any())).thenThrow(TimeoutException.class);
        assertThrows(TimeoutException.class, () -> testWebDriverWrapper.findElement(By.id("elementDoesNotExist")));
    }

    @Test
    public void testGetPageSource() {
        var testPageSource = "<html><div></div><body></body></html>";
        when(mockBaseWebDriver.getPageSource()).thenReturn(testPageSource);
        assertEquals(testPageSource, testWebDriverWrapper.getPageSource());
    }

    @Test
    public void testGetTitle() {
        var testTitle = "HAAAANNNNNNN SOOOLLLOOOOOOOOO!";
        when(mockBaseWebDriver.getTitle()).thenReturn(testTitle);
        assertEquals(testTitle, testWebDriverWrapper.getTitle());
    }

    @Test
    public void testSetAutomationWaitTimeout() {
        var tenSeconds = 10L;
        testWebDriverWrapper.getAutomationWait().setTimeoutInSeconds(tenSeconds);
        var automationWaitInSeconds = testWebDriverWrapper.getAutomationWait().getTimeoutInSeconds();
        assertEquals(tenSeconds, automationWaitInSeconds);
    }

    protected TestWebDriverWrapper createTestWebDriverWrapper() {
        return new TestWebDriverWrapper(mockBaseWebDriver, 1L);
    }
}
