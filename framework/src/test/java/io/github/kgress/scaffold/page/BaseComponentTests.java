package io.github.kgress.scaffold.page;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.kgress.scaffold.MockBaseWebElement;
import io.github.kgress.scaffold.MockComponent;
import io.github.kgress.scaffold.exception.ComponentException;
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
}
