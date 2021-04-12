package io.github.kgress.scaffold.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.exception.WebDriverManagerException;
import io.github.kgress.scaffold.models.enums.desktop.BrowserType;
import io.github.kgress.scaffold.models.enums.desktop.Platform;
import io.github.kgress.scaffold.models.enums.desktop.RunType;
import io.github.kgress.scaffold.models.enums.desktop.ScreenResolution;
import io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName;
import io.github.kgress.scaffold.models.enums.mobileemulator.MobilePlatform;
import io.github.kgress.scaffold.models.enums.mobileemulator.SauceDeviceName;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * This test class doesn't requires any Spring Boot context. Because of that, we can use a runner
 * independent of {@link BaseUnitTest}.
 */
@Slf4j
@Execution(ExecutionMode.CONCURRENT)
public class WebDriverValidationUtilTests {

  private final static String TEST_BINARY_PATH = "/tmp/headless-chromium";
  private final static String TEST_ACCESS_KEY = "testAccessKey";
  private final static String TEST_USERNAME = "testUsername";
  private DesiredCapabilitiesConfigurationProperties caps = new DesiredCapabilitiesConfigurationProperties();

  @Test
  public void testAwsCaps_emptyBinaryPathAndBrowser_fails() {
    caps.setRunType(RunType.AWS_LAMBDA_REMOTE);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateAwsLambdaDesiredCapabilities(caps));
  }

  @Test
  public void testAwsCaps_emptyBrowser_fails() {
    caps.setRunType(RunType.AWS_LAMBDA_REMOTE);
    caps.getAwsLambda().setBrowserBinaryPath(TEST_BINARY_PATH);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateAwsLambdaDesiredCapabilities(caps));
  }

  @Test
  public void testAwsCaps_succeeds() {
    caps.setRunType(RunType.AWS_LAMBDA_REMOTE);
    caps.setBrowserType(BrowserType.CHROME);
    caps.getAwsLambda().setBrowserBinaryPath(TEST_BINARY_PATH);
    assertDoesNotThrow(() ->
        WebDriverValidationUtil.validateAwsLambdaDesiredCapabilities(caps));
  }

  @Test
  public void testDesktopCaps_emptyRunPlatform_fails() {
    caps.setRunType(RunType.LOCAL);
    caps.setBrowserType(BrowserType.CHROME);
    caps.setScreenResolution(ScreenResolution.SIZE_1024x1080);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredDesktopBrowserCapabilities(caps));
  }

  @Test
  public void testDesktopCaps_emptyBrowserType_fails() {
    caps.setRunType(RunType.LOCAL);
    caps.setRunPlatform(Platform.Mac);
    caps.setScreenResolution(ScreenResolution.SIZE_1024x1080);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredDesktopBrowserCapabilities(caps));
  }

  @Test
  public void testDesktopCaps_emptyRunType() {
    caps.setRunPlatform(Platform.Mac);
    caps.setBrowserType(BrowserType.CHROME);
    caps.setScreenResolution(ScreenResolution.SIZE_1024x1080);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredDesktopBrowserCapabilities(caps));
  }

  /**
   * {@link DesiredCapabilitiesConfigurationProperties#getScreenResolution()} has a default set to
   * {@link ScreenResolution#SIZE_1024x768}.
   */
  @Test
  public void testDesktopCaps_emptyScreenResolution_succeeds() {
    caps.setRunType(RunType.LOCAL);
    caps.setBrowserType(BrowserType.CHROME);
    caps.setRunPlatform(Platform.Mac);
    assertEquals(ScreenResolution.SIZE_1024x1080, caps.getScreenResolution());
    assertDoesNotThrow(() ->
        WebDriverValidationUtil.validateRequiredDesktopBrowserCapabilities(caps));
  }

  @Test
  public void testDesktopCaps_success() {
    caps.setRunType(RunType.LOCAL);
    caps.setRunPlatform(Platform.Mac);
    caps.setBrowserType(BrowserType.CHROME);
    caps.setScreenResolution(ScreenResolution.SIZE_1024x1080);
    assertDoesNotThrow(() ->
        WebDriverValidationUtil.validateRequiredDesktopBrowserCapabilities(caps));
  }

  @Test
  public void testMobileCaps_emptyDeviceName_fails() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setPlatformName(MobilePlatform.ANDROID);
    caps.getMobile().setBrowserName(MobileBrowserName.CHROME);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testMobileCaps_emptyBrowserName_fails() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setSauceDeviceName(SauceDeviceName.SAMSUNG_GALAXY_S9_HD);
    caps.getMobile().setPlatformName(MobilePlatform.ANDROID);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testMobileCaps_emptyPlatformName_fails() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setSauceDeviceName(SauceDeviceName.SAMSUNG_GALAXY_S9_HD);
    caps.getMobile().setBrowserName(MobileBrowserName.CHROME);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testMobileCaps_succeeds() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setSauceDeviceName(SauceDeviceName.SAMSUNG_GALAXY_S9_HD);
    caps.getMobile().setPlatformName(MobilePlatform.ANDROID);
    caps.getMobile().setBrowserName(MobileBrowserName.CHROME);
    assertDoesNotThrow(() ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testMobileCaps_androidSafari_fails() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setSauceDeviceName(SauceDeviceName.SAMSUNG_GALAXY_S9_HD);
    caps.getMobile().setPlatformName(MobilePlatform.ANDROID);
    caps.getMobile().setBrowserName(MobileBrowserName.SAFARI);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testMobileCaps_iosChrome_fails() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setSauceDeviceName(SauceDeviceName.SAMSUNG_GALAXY_S9_HD);
    caps.getMobile().setPlatformName(MobilePlatform.IOS);
    caps.getMobile().setBrowserName(MobileBrowserName.CHROME);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testMobileCaps_iosAndroid_fails() {
    caps.setRunType(RunType.SAUCE_MOBILE_EMULATOR);
    caps.getMobile().setSauceDeviceName(SauceDeviceName.SAMSUNG_GALAXY_S9_HD);
    caps.getMobile().setPlatformName(MobilePlatform.IOS);
    caps.getMobile().setBrowserName(MobileBrowserName.ANDROID);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities(caps));
  }

  @Test
  public void testSauceCaps_emptyUsername_fails() {
    caps.setRunType(RunType.SAUCE);
    caps.getSauce().setAccessKey(TEST_ACCESS_KEY);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredSauceAuth(caps));
  }

  @Test
  public void testSauceCaps_emptyAccessKey_fails() {
    caps.setRunType(RunType.SAUCE);
    caps.getSauce().setUserName(TEST_USERNAME);
    assertThrows(WebDriverManagerException.class, () ->
        WebDriverValidationUtil.validateRequiredSauceAuth(caps));
  }

  @Test
  public void testSauceCaps_succeeds() {
    caps.setRunType(RunType.SAUCE);
    caps.getSauce().setUserName(TEST_USERNAME);
    caps.getSauce().setAccessKey(TEST_ACCESS_KEY);
    assertDoesNotThrow(() ->
        WebDriverValidationUtil.validateRequiredSauceAuth(caps));
  }
}
