package io.github.kgress.scaffold;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.environment.config.ScaffoldConfiguration;
import io.github.kgress.scaffold.webdrivercontext.WebDriverContextTests;
import io.github.kgress.scaffold.webelements.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;

@ActiveProfiles("unit_testing")
@Slf4j
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith({
        SpringExtension.class,
        MockitoExtension.class})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { ScaffoldConfiguration.class }
)
public abstract class BaseUnitTest {

    @Mock
    protected WebElement mockRawWebElement;

    @Mock
    protected WebElement mockParentRawWebElement;

    @Mock
    protected WebDriver mockBaseWebDriver;

    @Mock
    protected WebDriverWrapper mockWebDriverWrapper;

    @Mock
    protected WebElementWait mockWebElementWait;

    @Mock
    protected JavascriptExecutor mockJavascriptExecutor;

    @Mock
    protected Select mockSelect;

    @Autowired
    protected DesiredCapabilitiesConfigurationProperties desiredCapabilities;

    @Autowired
    protected RestTemplate seleniumGridRestTemplate;

    /**
     * A helper method to set a base level when from {@link org.mockito.Mockito}. The reason it's being used as
     * a helper method instead of in a BeforeEach step is because some tests in test files don't require stubbing like
     * this prior to the test running. Mockito wasn't very happy about the stubbing existing when it didn't need it.
     * Thus, the birth of this helper.
     *
     * @param element   the element that's being interacted with
     * @param <T>       the type reference as an extension of {@link BaseWebElement}
     */
    protected <T extends BaseWebElement> void setBaseWhen(T element) {
        when(mockWebElementWait.waitUntilDisplayed()).thenReturn(mockRawWebElement);
        when(element.getRawWebElement()).thenReturn(mockRawWebElement);
    }

    /**
     * A helper method to set a when from {@link org.mockito.Mockito} when we're wanting to invoke the scroll into
     * view script and expect it to succeed.
     */
    protected void setWhenScrollIntoViewSucceed() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when((WebElement) mockWebDriverWrapper
                .getJavascriptExecutor()
                .executeScript(SharedTestVariables.SCROLL_INTO_VIEW_SCRIPT, mockRawWebElement))
                .thenReturn(mockRawWebElement);
    }

    /**
     * A helper method to set a when from {@link org.mockito.Mockito} when we're wanting to invoke the scroll into
     * view script and expect it to fail.
     */
    protected void setWhenScrollIntoViewFail() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when((WebElement) mockWebDriverWrapper
                .getJavascriptExecutor()
                .executeScript(SharedTestVariables.SCROLL_INTO_VIEW_SCRIPT, mockRawWebElement))
                .thenThrow(TimeoutException.class);
    }

    /**
     * A helper method to set a when from {@link org.mockito.Mockito} when we're wanting to invoke the get raw parent
     * web element script and expect it to succeed.
     */
    protected void setWhenGetRawParentElementSucceed() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when((WebElement) mockWebDriverWrapper
                .getJavascriptExecutor()
                .executeScript(SharedTestVariables.PARENT_ELEMENT_SCRIPT, mockRawWebElement))
                .thenReturn(mockParentRawWebElement);
    }

    /**
     * A helper method to set a when from {@link org.mockito.Mockito} when we're wanting to invoke the get raw parent
     * web element script and expect it to fail.
     */
    protected void setWhenGetRawParentElementFail() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when((WebElement) mockWebDriverWrapper
                .getJavascriptExecutor()
                .executeScript(SharedTestVariables.PARENT_ELEMENT_SCRIPT, mockRawWebElement))
                .thenThrow(TimeoutException.class);
    }

    /**
     * A test class required for configuring a web driver in {@link WebDriverContextTests}. The super requires package
     * level access.
     */
    protected static class TestWebDriverManager extends WebDriverManager {
        public TestWebDriverManager(DesiredCapabilitiesConfigurationProperties desiredCapabilities,
                                    RestTemplate seleniumGridRestTemplate) {
            super(desiredCapabilities, seleniumGridRestTemplate);
        }

        public void initDriver_fromParent(String testName) {
            initDriver(testName);
        }

        public WebDriverWrapper getWebDriverWrapper_fromParent() {
            return getWebDriverWrapper();
        }
    }

    /**
     * A test class required for configuring a web driver in {@link WebDriverContextTests}. The super requires package
     * level access.
     */
    protected static class TestWebDriverWrapper extends WebDriverWrapper {
        public TestWebDriverWrapper(WebDriver baseWebDriver, Long waitTimeoutInSeconds) {
            super(baseWebDriver, waitTimeoutInSeconds);
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestBaseWebElement extends BaseWebElement {
        public TestBaseWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestBaseWebElement(By by) {
            super(by);
        }

        public TestBaseWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestBaseClickableWebElement extends BaseClickableWebElement {
        public TestBaseClickableWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestBaseClickableWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        public TestBaseClickableWebElement(By by) {
            super(by);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestBaseClickableAndTypableWebElement extends BaseClickableAndTypableWebElement {
        public TestBaseClickableAndTypableWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestBaseClickableAndTypableWebElement(By by) {
            super(by);
        }

        public TestBaseClickableAndTypableWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestDivWebElement extends DivWebElement {
        public TestDivWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestDivWebElement(By by) {
            super(by);
        }

        public TestDivWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestButtonWebElement extends ButtonWebElement {
        public TestButtonWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestButtonWebElement(By by) {
            super(by);
        }

        public TestButtonWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestCheckboxWebElement extends CheckBoxWebElement {
        public TestCheckboxWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestCheckboxWebElement(By by) {
            super(by);
        }

        public TestCheckboxWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestImageWebElement extends ImageWebElement {
        public TestImageWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestImageWebElement(By by) {
            super(by);
        }

        public TestImageWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestInputWebElement extends InputWebElement {
        public TestInputWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestInputWebElement(By by) {
            super(by);
        }

        public TestInputWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestLinkWebElement extends LinkWebElement {
        public TestLinkWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestLinkWebElement(By by) {
            super(by);
        }

        public TestLinkWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestStaticTextWebElement extends StaticTextWebElement {
        public TestStaticTextWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestStaticTextWebElement(By by) {
            super(by);
        }

        public TestStaticTextWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestDropDownWebElement extends DropDownWebElement {
        public TestDropDownWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestDropDownWebElement(By by) {
            super(by);
        }

        public TestDropDownWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }

        @Override
        public Select getSelectElement() {
            return mockSelect;
        }
    }

    /**
     * A nested class for testing. It's living in {@link BaseUnitTest} because it requires package access. it also
     * requires some overrides, so we can return mocks instead of invoking the real method calls.
     */
    public class TestRadioWebElement extends RadioWebElement {

        public TestRadioWebElement(String cssSelector) {
            super(cssSelector);
        }

        public TestRadioWebElement(By by) {
            super(by);
        }

        public TestRadioWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        @Override
        public WebDriverWrapper getWebDriverWrapper() {
            return mockWebDriverWrapper;
        }

        @Override
        public void setWebElementWait() {}

        @Override
        public WebElementWait getWebElementWait() {
            return mockWebElementWait;
        }
    }
}
