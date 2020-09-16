package io.github.kgress.scaffold.environment.config;

import io.github.kgress.scaffold.models.enums.BrowserType;
import io.github.kgress.scaffold.models.enums.Platform;
import io.github.kgress.scaffold.models.enums.RunType;
import io.github.kgress.scaffold.models.enums.ScreenResolution;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import static io.github.kgress.scaffold.models.enums.ScreenResolution.SIZE_1024x1080;

/**
 * This model depicts the many Desired Capabilities of a Selenium WebDriver browser. This is used as an auto configuration
 * for implementing projects.
 *
 * {@link BrowserType} and {@link RunType}.
 */
@Validated
@ConfigurationProperties(prefix = "desired-capabilities")
public class DesiredCapabilitiesConfigurationProperties {

    @NotNull
    private BrowserType browserType;

    @NotNull
    private RunType runType;

    private String remoteUrl;
    private String browserVersion = ""; // Empty represents latest version
    private Platform runPlatform;
    private ScreenResolution screenResolution = SIZE_1024x1080; // default screen size
    private boolean uploadScreenshots = false;
    private boolean useProxy = false;
    private final SauceContext sauce = new SauceContext();

    public BrowserType getBrowserType() {
        return browserType;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public Platform getRunPlatform() {
        return runPlatform;
    }

    public RunType getRunType() {
        return runType;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public SauceContext getSauce() {
        return sauce;
    }

    public boolean getUploadScreenshots() {
        return uploadScreenshots;
    }

    public boolean getUseProxy() {
        return useProxy;
    }

    public ScreenResolution getScreenResolution() {
        return screenResolution;
    }

    /**
     * The type of browser to be used. Chrome, Opera, Safari, etc.
     * <p>
     * This should never be null.
     */
    public void setBrowserType(BrowserType browserType) {
        this.browserType = browserType;
    }

    /**
     * The version of the browser to be used. This assumes the user implementing this project knows the versions. If
     * the wrong version of a browser is passed in, there is a default somewhere that sets the browser version to
     * latest.
     * <p>
     * TODO think about updating this to an enum
     */
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    /**
     * The operating system to be used. Linux, MacOS, Windows, Opera, etc.
     * <p>
     */
    public void setRunPlatform(Platform runPlatform) {
        this.runPlatform = runPlatform;
    }

    /**
     * The type of run that is being used, depicted by {@link RunType}. This can be {@link RunType#SAUCE},
     * {@link RunType#LOCAL}, or {@link RunType#GRID}.
     */
    public void setRunType(RunType runType) {
        this.runType = runType;
    }

    public void setScreenResolution(ScreenResolution screenResolution) {
        this.screenResolution = screenResolution;
    }

    /**
     * The default grid URL to use.
     * <p>
     * Right now, this value cannot be null due to the use of this value in the bean creation of the Grid rest template.
     */
    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    /**
     * A boolean for choosing to have the remote web driver upload screen shots.
     */
    public void setUploadScreenshots(boolean uploadScreenshots) {
        this.uploadScreenshots = uploadScreenshots;
    }

    /**
     * A boolean for choosing to use proxy.
     */
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    /**
     * This is a nested class that exists within {@link DesiredCapabilitiesConfigurationProperties}. This is used as an auto configuration
     *  * for implementing projects.
     */
    public class SauceContext {

        private String url;
        private String userName;
        private String password;
        private String accessKey;
        private String tunnelIdentifier;
        private String parentTunnel;
        private String timeZone;

        public String getUrl() {
            return url;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public String getTunnelIdentifier() {
            return tunnelIdentifier;
        }

        public String getParentTunnel() { return parentTunnel; }

        public String getTimeZone() { return timeZone; }

        /**
         * The main sauce URL to be used. In the event this ever changes, we'll let the implementing project handle
         * setting this.
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * The username of the sauce account. Users should implement a means to not hardcode this information in their
         * implementing project.
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * The password of the sauce account. Users should implement a means to not hardcode this information in their
         * implementing project.
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * The secret access key that is assigned to the sauce username and password. Users should implement a means to
         * not hardcode this information in their implementing project.
         */
        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        /**
         * The name of the tunnel that is being used to allow sauce labs to connect to your lower environments.
         */
        public void setTunnelIdentifier(String tunnelIdentifier) {
            this.tunnelIdentifier = tunnelIdentifier;
        }

        /**
         * The name of the user that owns tunnel that is being used to allow sauce labs to connect to your lower environments.
         */
        public void setParentTunnel(String parentTunnel) {
            this.parentTunnel = parentTunnel;
        }

        /**
         * The custom time zone to use when spinning up the sauce lab VM.
         */
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }
}
