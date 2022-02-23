package io.github.kgress.scaffold;

import java.util.List;
import lombok.Getter;
import org.mockito.Mock;
import org.openqa.selenium.By;

/**
 * Required mock class for testing the {@link BaseComponent}. Cannot be nested since we're using
 * reflection on this class.
 */
@Getter
public class MockComponent extends BaseComponent {

  @Mock
  private AutomationWait mockAutomationWait;

  private final MockBaseWebElement testField = new MockBaseWebElement(By.cssSelector("#fake-field"));
  private final MockBaseWebElement testField2 = new MockBaseWebElement(By.cssSelector("#fake-field-2"));

  @Override
  public AutomationWait getAutomationWait() {
    return mockAutomationWait;
  }

  public <T extends BaseComponent, X extends BaseWebElement> List<T> buildComponentList_callProtectedMethod(List<X> listOfElements, Class<T> component) {
    return buildComponentList(listOfElements, component);
  }
}
