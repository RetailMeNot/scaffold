package io.github.kgress.scaffold.util;

import static io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName.ANDROID;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName.CHROME;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName.SAFARI;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobilePlatform.IOS;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties.MobileEmulator;
import io.github.kgress.scaffold.exception.WebDriverManagerException;
import io.github.kgress.scaffold.models.enums.desktop.BrowserType;
import io.github.kgress.scaffold.models.enums.desktop.RunType;
import io.github.kgress.scaffold.models.enums.mobileemulator.MobilePlatform;
import io.github.kgress.scaffold.webdriver.WebDriverManager;
import java.util.Optional;

/**
 * This class is used in {@link WebDriverManager} to validate required properties based on the
 * {@link DesiredCapabilitiesConfigurationProperties#getRunType()}.
 */
public class WebDriverValidationUtil {

  /**
   * Performs validation on {@link RunType#AWS_LAMBDA_REMOTE} and {@link RunType#AWS_LAMBDA_LOCAL}.
   */
  public static void validateAwsLambdaDesiredCapabilities(
      DesiredCapabilitiesConfigurationProperties desiredCapabilities) {
    var browserBinaryPath = Optional
        .ofNullable(desiredCapabilities.getAwsLambda().getBrowserBinaryPath());
    var browserType = desiredCapabilities.getBrowserType(); // already null checked

    if (browserBinaryPath.isEmpty()) {
      throw new WebDriverManagerException(
          "Could not find the browser binary path for AWS Lambda run. "
              + "Please check your configuration and try again.");
    }

    if (browserType != BrowserType.CHROME) {
      throw new WebDriverManagerException(
          "AWS Lambda currently only supports headless chrome. "
              + "Please check your configuration and try again.");
    }
  }

  /**
   * Performs validation on {@link RunType#LOCAL}, {@link RunType#HEADLESS}, {@link RunType#GRID},
   * {@link RunType#SAUCE}, {@link RunType#AWS_LAMBDA_REMOTE}, and {@link RunType#AWS_LAMBDA_LOCAL}
   */
  public static void validateRequiredDesktopBrowserCapabilities(
      DesiredCapabilitiesConfigurationProperties desiredCapabilities) {
    var runType = Optional
        .ofNullable(desiredCapabilities.getRunType());
    var runPlatform = Optional
        .ofNullable(desiredCapabilities.getRunPlatform());
    var browserType = Optional
        .ofNullable(desiredCapabilities.getBrowserType());
    var screenResolution = Optional
        .ofNullable(desiredCapabilities.getScreenResolution());

    if (runPlatform.isEmpty()) {
      throw new WebDriverManagerException(
          "Run Platform must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
    if (browserType.isEmpty()) {
      throw new WebDriverManagerException(
          "Browser type must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
    // In case screen resolution ever is not set as default, this will catch the change in the future.
    if (screenResolution.isEmpty()) {
      throw new WebDriverManagerException(
          "Screen Resolution must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
    // In case run type ever is not checked by lombok, this will catch the change in the future.
    if (runType.isEmpty()) {
      throw new WebDriverManagerException(
          "Run Type must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
  }

  /**
   * Checks the required values from the {@link MobileEmulator} to ensure they are not null. Also
   * checks to ensure a mismatch is not present between the platform and browser. We are opting not
   * to perform error checks against the device name, os, and browser name since there an exorbitant
   * amount of combinations. Instead, we will surface the sauce error to the user.
   */
  public static void validateRequiredMobileEmulatorCapabilities(
      DesiredCapabilitiesConfigurationProperties desiredCapabilities) {
    var deviceName = Optional
        .ofNullable(desiredCapabilities.getMobile().getSauceDeviceName());
    var browserName = Optional
        .ofNullable(desiredCapabilities.getMobile().getBrowserName());
    var platformName = Optional
        .ofNullable(desiredCapabilities.getMobile().getPlatformName());

    var mismatchedBrowserAndPlatformException =
        new WebDriverManagerException(String.format(
            "Operating system and browser mismatch: platformName = %s, browserName = %s. "
                + "Please check your configuration and try again.",
            platformName, browserName));

    if (deviceName.isEmpty()) {
      throw new WebDriverManagerException(
          "Device Name must be defined when initiating a mobile emulator web driver configuration. "
              + "Please check your configuration and try again.");
    }
    if (browserName.isEmpty()) {
      throw new WebDriverManagerException(
          "Browser Name must be defined when initiating a mobile emulator web driver configuration. "
              + "Please check your configuration and try again.");
    }
    if (platformName.isEmpty()) {
      throw new WebDriverManagerException(
          "Platform Name must be defined when initiating a mobile emulator web driver configuration. "
              + "Please check your configuration and try again.");
    }

    // Some high level validation so we can fail faster
    if ((platformName.get() == MobilePlatform.ANDROID) && (browserName.get() == SAFARI)) {
      throw mismatchedBrowserAndPlatformException;
    }

    if ((platformName.get() == IOS) && (browserName.get() == CHROME)) {
      throw mismatchedBrowserAndPlatformException;
    }

    if ((platformName.get() == IOS) && (browserName.get() == ANDROID)) {
      throw mismatchedBrowserAndPlatformException;
    }
  }

  /**
   * Performs validation on {@link RunType#SAUCE} and {@link RunType#SAUCE_MOBILE_EMULATOR}.
   */
  public static void validateRequiredSauceAuth(
      DesiredCapabilitiesConfigurationProperties desiredCapabilities) {
    var username = Optional.ofNullable(desiredCapabilities.getSauce().getUserName());
    var accessKey = Optional.ofNullable(desiredCapabilities.getSauce().getAccessKey());

    if (username.isEmpty()) {
      throw new WebDriverManagerException(
          "Username must be defined when initiating a Sauce based browser configuration. "
              + "Please check your configuration and try again.");
    }
    if (accessKey.isEmpty()) {
      throw new WebDriverManagerException(
          "Access Key must be defined when initiating a Sauce based browser configuration. "
              + "Please check your configuration and try again.");
    }
  }
}
