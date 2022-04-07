package io.github.kgress.scaffold.webelement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.kgress.scaffold.BaseWebElement;
import io.github.kgress.scaffold.MockBaseWebElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class BaseWebElementLoggerTests {

  private final TestLogger testLogger = TestLoggerFactory.getTestLogger(BaseWebElement.class);

  @BeforeEach
  public void clearLogsBefore() {
    testLogger.clearAll();
  }

  @AfterEach
  public void clearLogsAfter() {
    testLogger.clearAll();
  }

  @Test
  public void testXpathConstructorAddsLog_singleBy() {
    var mockBaseWebElement = new MockBaseWebElement(By.xpath("fake-xpath"));
    assertNotNull(testLogger.getLoggingEvents());
    assertEquals(1, testLogger.getLoggingEvents().size());

    final var exceptionMessage = testLogger.getLoggingEvents().get(0).getMessage();
    final var logLevel = testLogger.getLoggingEvents().get(0).getLevel();
    assertEquals(Level.WARN, logLevel);
    assertEquals("It is strongly recommended to use a CSS selector for elements "
        + "instead of XPATH when instantiating new Scaffold elements. Failure in "
        + "using a CSS selector may hinder the availability of functionality on the "
        + "element.", exceptionMessage);
  }

  @Test
  public void testWebElementConstructorAddsLog_webElement() {
    var mockWebElement = Mockito.mock(WebElement.class);
    new MockBaseWebElement(mockWebElement);
    assertNotNull(testLogger.getLoggingEvents());
    assertEquals(1, testLogger.getLoggingEvents().size());

    final var exceptionMessage = testLogger.getLoggingEvents().get(0).getMessage();
    final var logLevel = testLogger.getLoggingEvents().get(0).getLevel();
    assertEquals(Level.ERROR, logLevel);
    assertEquals(String.format("Usage of element [%s] with a WebElement constructor "
        + "bypasses core Scaffold functionality and will result in unpredictable "
        + "behavior. Please instantiate new Scaffold elements with a constructor that "
        + "does not have a WebElement.", mockWebElement), exceptionMessage);
  }

  @Test
  public void testWebElementConstructorAddsLog_byAndWebElement() {
    var mockWebElement = Mockito.mock(WebElement.class);
    new MockBaseWebElement(By.cssSelector("fake-css"), mockWebElement);
    assertNotNull(testLogger.getLoggingEvents());
    assertEquals(1, testLogger.getLoggingEvents().size());

    final var exceptionMessage = testLogger.getLoggingEvents().get(0).getMessage();
    final var logLevel = testLogger.getLoggingEvents().get(0).getLevel();
    assertEquals(Level.ERROR, logLevel);
    assertEquals(String.format("Usage of element [%s] with a WebElement constructor "
        + "bypasses core Scaffold functionality and will result in unpredictable "
        + "behavior. Please instantiate new Scaffold elements with a constructor that "
        + "does not have a WebElement.", mockWebElement), exceptionMessage);
  }

  @Test
  public void testWebElementConstructorAddsLog_byAndWebElementAndParentBy() {
    var mockWebElement = Mockito.mock(WebElement.class);
    new MockBaseWebElement(By.cssSelector("fake-css-child"),
        By.cssSelector("fake-css-parent"), mockWebElement);
    assertNotNull(testLogger.getLoggingEvents());
    assertEquals(1, testLogger.getLoggingEvents().size());

    final var exceptionMessage = testLogger.getLoggingEvents().get(0).getMessage();
    final var logLevel = testLogger.getLoggingEvents().get(0).getLevel();
    assertEquals(Level.ERROR, logLevel);
    assertEquals(String.format("Usage of element [%s] with a WebElement constructor "
        + "bypasses core Scaffold functionality and will result in unpredictable "
        + "behavior. Please instantiate new Scaffold elements with a constructor that "
        + "does not have a WebElement.", mockWebElement), exceptionMessage);
  }
}
