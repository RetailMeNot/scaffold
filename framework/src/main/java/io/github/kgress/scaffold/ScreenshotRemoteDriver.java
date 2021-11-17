package io.github.kgress.scaffold;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

/**
 * Wrapper that allows for screenshots to be taken at will within a {@link RemoteWebDriver}.
 */
public class ScreenshotRemoteDriver extends RemoteWebDriver implements TakesScreenshot {

    public ScreenshotRemoteDriver(URL remoteAddress, Capabilities desiredCapabilities) {
        super(remoteAddress, desiredCapabilities);
    }

    /**
     * Captures a screenshot and returns it with the provided Type Reference.
     *
     * @param target the format of the file.
     * @param <X> the Type Reference .
     * @return the screenshot by the provided Type Reference.
     * @throws WebDriverException the exception to be thrown if it's unable to capture the screenshot.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return target.convertFromBase64Png(execute(DriverCommand.SCREENSHOT).getValue().toString());
    }
}
