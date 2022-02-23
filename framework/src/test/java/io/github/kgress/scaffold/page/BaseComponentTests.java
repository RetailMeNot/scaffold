package io.github.kgress.scaffold.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.kgress.scaffold.MockBaseWebElement;
import io.github.kgress.scaffold.MockComponent;
import io.github.kgress.scaffold.exception.ComponentException;
import io.github.kgress.scaffold.util.AutomationUtils;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class BaseComponentTests {

  private MockComponent MockComponent;

  @BeforeEach
  public void setup() {
    MockComponent = new MockComponent();
  }

  @Test
  public void testBuildComponentList_xpathAsBy() {
    final var xpathElement = new MockBaseWebElement(By.xpath("fake xpath"));
    final var listOfElements = Arrays.asList(xpathElement, xpathElement);

    assertThrows(ComponentException.class, () ->
        MockComponent.buildComponentList_callProtectedMethod(listOfElements, MockComponent.class));
  }

  @Test
  public void testBuildComponentList_xpathAsParent() {
    final var xpathParentElement = new MockBaseWebElement(By.cssSelector("#css-child"),
        By.xpath("xpath parent"));
    final var listOfElements = Arrays.asList(xpathParentElement, xpathParentElement);
    assertThrows(ComponentException.class, () ->
        MockComponent.buildComponentList_callProtectedMethod(listOfElements, MockComponent.class));
  }

  @Test
  public void testBuildComponentList_xpathAsChild() {
    final var xpathChildElement = new MockBaseWebElement(By.xpath("xpath child"),
        By.cssSelector("#css-parent"));
    final var listOfElements = Arrays.asList(xpathChildElement, xpathChildElement);
    assertThrows(ComponentException.class, () ->
        MockComponent.buildComponentList_callProtectedMethod(listOfElements, MockComponent.class));
  }

  @Test
  public void testBuildComponentList_cssAsBy() {
    final var expectedGetByIndex0 = "#fake-parent #fake-child:nth-child(1) #fake-field";
    final var expectedGetByIndex1 = "#fake-parent #fake-child:nth-child(2) #fake-field";
    final var cssParentAndChild = new MockBaseWebElement(
        By.cssSelector("#fake-parent #fake-child"));
    final var listOfElements = Arrays.asList(cssParentAndChild, cssParentAndChild);
    final var output = MockComponent.buildComponentList_callProtectedMethod(listOfElements,
        MockComponent.class);
    assertEquals(2, output.size());
    assertEquals(expectedGetByIndex0,
        AutomationUtils.getUnderlyingLocatorByString(output.get(0).getTestField().getBy()));
    assertEquals(expectedGetByIndex1,
        AutomationUtils.getUnderlyingLocatorByString(output.get(1).getTestField().getBy()));
  }
}
