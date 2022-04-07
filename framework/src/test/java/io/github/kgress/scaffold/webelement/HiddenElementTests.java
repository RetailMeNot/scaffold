package io.github.kgress.scaffold.webelement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.kgress.scaffold.BaseComponent;
import io.github.kgress.scaffold.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Slf4j
public class HiddenElementTests extends BaseUnitTest {

    class TestComponent extends BaseComponent {

        private final TestBaseWebElement hiddenThing = new TestBaseWebElement(
                By.cssSelector(".hiddenElement"),null, true);

        private final TestBaseWebElement notHiddenThing = new TestBaseWebElement(
                By.cssSelector(".notHidden"));

        public TestBaseWebElement getHiddenThing() {
            return hiddenThing;
        }

        public TestBaseWebElement getNotHiddenThing() {
            return notHiddenThing;
        }
    }

    @Test
    public void testHiddenElement() {
        final var testComponent = new TestComponent();
        when(testComponent.hiddenThing.getRawWebElement()).thenReturn(mock(WebElement.class));
        testComponent.getHiddenThing().getText();
        verifyNoInteractions(testComponent.hiddenThing.getWebElementWait());
    }

    @Test
    public void testNotHiddenElement() {
        final var testComponent = new TestComponent();
        when(testComponent.notHiddenThing.getRawWebElement()).thenReturn(mock(WebElement.class));
        testComponent.getNotHiddenThing().getText();
        verify(testComponent.notHiddenThing.getWebElementWait(), times(2)).waitUntilDisplayed();
    }

}

