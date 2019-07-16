package org.scaffold.models.enums;

/**
 * A simple enum to differentiate the many OS's that Selenium supports.
 */
public enum Platform {

    Windows("WINDOWS"),
    XP("XP"),
    Vista("VISTA"),
    Mac("MAC"),
    Linux("LINUX"),
    Unix("UNIX"),
    Android("ANDROID"),
    iOS("IOS");

    private final String platform;

    Platform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }
}
