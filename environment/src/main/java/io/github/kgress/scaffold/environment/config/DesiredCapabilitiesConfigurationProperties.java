package io.github.kgress.scaffold.environment.config;

import static io.github.kgress.scaffold.models.enums.desktop.ScreenResolution.SIZE_1024x1080;

import io.github.kgress.scaffold.models.enums.desktop.BrowserType;
import io.github.kgress.scaffold.models.enums.desktop.Platform;
import io.github.kgress.scaffold.models.enums.desktop.RunType;
import io.github.kgress.scaffold.models.enums.desktop.ScreenResolution;
import io.github.kgress.scaffold.models.enums.mobileemulator.DeviceOrientation;
import io.github.kgress.scaffold.models.enums.mobileemulator.DeviceType;
import io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName;
import io.github.kgress.scaffold.models.enums.mobileemulator.MobilePlatform;
import io.github.kgress.scaffold.models.enums.mobileemulator.SauceDeviceName;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * This model depicts the many Desired Capabilities of a Selenium WebDriver browser. This is used as
 * an auto configuration for implementing projects.
 * <p>
 * {@link BrowserType} and {@link RunType}.
 */
@Validated
@ConfigurationProperties(prefix = "desired-capabilities")
@Getter
@Setter
public class DesiredCapabilitiesConfigurationProperties {

  /**
   * Required. The type of run that is being used, depicted by {@link RunType}. This can be {@link
   * RunType#SAUCE}, {@link RunType#LOCAL} for scaffold unit testing, or {@link RunType#GRID}.
   * <p>
   * This is a bare minimum required field when executing a test run. Lombok provides a null check
   * with the annotation {@link NonNull}. There are other null checks for proper configuration of
   * the desired capabilities included in the WebDriverManager.
   */
  @NonNull
  private RunType runType;

  /**
   * Required. The operating system to be used as defined by the enum {@link Platform}. Linux,
   * MacOS, Windows, Opera, etc. Since test execution can be either desktop or mobile, setting this
   * to {@link NonNull} at the field level is a bad idea. Instead, we check for non null on this
   * field in WebDriverManager.
   */
  private Platform runPlatform;

  /**
   * Required. The type of browser to be used as defined by the enum {@link BrowserType}. Chrome,
   * Opera, Safari, etc.
   * <p>
   * If running a desktop test execution, this field should never be null. Since test execution can
   * be either desktop or mobile, setting this to {@link NonNull} at the field level is a bad idea.
   * Instead, we check for non null on this field in WebDriverManager.
   */
  private BrowserType browserType;

  /**
   * Optional. The version of the browser to be used. This assumes the user implementing this
   * project knows the versions. If the wrong version of a browser is passed in, there is a default
   * somewhere that sets the browser version to latest.
   * <p>
   * Leaving browser version set to empty automatically defaults to the latest version of the
   * browser. Change this version to your own string for a specific version of the browser. A full
   * list of supported browsers and version can be found here: https://saucelabs.com/platform/supported-browsers-devices
   * .
   */
  private String browserVersion = "";

  /**
   * Optional. The default remote URL to use. This can be used for execution against a remote web
   * browser that is not on your local system. For example, sending test executions to a docker
   * container or against a grid.
   */
  private String remoteUrl;

  /**
   * Optional. Sets the default screen resolution to 1024x1080. This can be easily overwritten by
   * including a screen resolution in your own spring profile.
   */
  private ScreenResolution screenResolution = SIZE_1024x1080;

  /**
   * Optional. Sets the upload screenshots capability to false. This can be easily overwritten by
   * including an upload screenshots desired capability in your spring profile set to true.
   */
  private boolean uploadScreenshots = false;

  /**
   * Not currently in use.
   * <p>
   * TODO the code base currently is not setting use proxy anywhere. We should look into offering
   * this functionality
   */
  private boolean useProxy = false;

  /**
   * Creates a new {@link SauceAuthentication} in the event there are sauce options for the test
   * execution.
   */
  private final SauceAuthentication sauce = new SauceAuthentication();

  /**
   * Creates a new {@link MobileEmulator} in the event there are mobile options for the test
   * execution.
   */
  private final MobileEmulator mobile = new MobileEmulator();

  /**
   * SauceAuthentication configuration properties are used for setting Sauce credentials up in your
   * Spring profile.
   */
  @Getter
  @Setter
  public static class SauceAuthentication {

    /**
     * Required. The username of the sauce account. Users should implement a means to not hardcode
     * this information in their implementing project.
     */
    private String userName;

    /**
     * Required. The password of the sauce account. Users should implement a means to not hardcode
     * this information in their implementing project.
     */
    private String password;

    /**
     * Required. The secret access key that is assigned to the sauce username and password. Users
     * should implement a means to not hardcode this information in their implementing project.
     */
    private String accessKey;

    /**
     * Optional. The name of the tunnel that is being used to allow sauce labs to connect to your
     * lower environments.
     */
    private String tunnelIdentifier;

    /**
     * Optional. The name of the user that owns tunnel that is being used to allow sauce labs to
     * connect to your lower environments.
     */
    private String parentTunnel;

    /**
     * Optional. The main sauce URL to be used. In the event this ever changes, we'll let the
     * implementing project handle setting this.
     */
    private String url;

    /**
     * Optional. The custom time zone to use when spinning up the sauce lab VM.
     */
    private String timeZone;
  }

  /**
   * MobileEmulator Configuration Properties are used for configuring a mobile emulator test
   * execution. With the first iteration of this, we will directly support {@link
   * SauceDeviceName}'s. Since other implementations, like local devices or amazon device farm, use
   * a different nomenclature for device names and include support for devices that sauce does not,
   * we should think about adding an enum or at least a custom field to support those use cases. We
   * could then add those enum's to this object and update the logic on WebDriverManager to handle
   * setting the desired capabilities based on the type of device name being loaded.
   */
  @Getter
  @Setter
  public static class MobileEmulator {

    /**
     * Required. The name of the simulator, emulator, or device you want to use in the test.
     * <p>
     * If a mobile emulation run is intended, this value should never be null. We check for non null
     * on this field in WebDriverManager.
     */
    private SauceDeviceName sauceDeviceName;

    /**
     * Required. The mobile web browser that will be automated in the simulator, emulator or
     * device.
     * <p>
     * If a mobile emulation run is intended, this value should never be null. We check for non null
     * on this field in WebDriverManager.
     */
    private MobileBrowserName browserName;

    /**
     * Required. The mobile operating system platform you want to use in your test.
     * <p>
     * If a mobile emulation run is intended, this value should never be null. We check for non null
     * on this field in WebDriverManager.
     */
    private MobilePlatform platformName;

    /**
     * Optional. The version of appium you'd like sauce to use when executing the test.
     */
    private String appiumVersion;

    /**
     * Optional. The mobile operating system version that you want to use in your test.
     */
    private String platformVersion;

    /**
     * Optional. The type of device to emulate.
     */
    private DeviceType deviceType;

    /**
     * Optional. The orientation in which the simulator/device will be rendered.
     */
    private DeviceOrientation deviceOrientation;
  }
}
