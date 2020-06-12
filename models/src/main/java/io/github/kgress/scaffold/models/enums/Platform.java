package io.github.kgress.scaffold.models.enums;

/**
 * A simple enum to differentiate the many OS's that Selenium supports.
 */
public enum Platform {

    WINDOWS("Windows"),
    WINDOWS_7("Windows 7"),
    WINDOWS_8("Windows 8"),
    WINDOWS_8_1("Windows 8.1"),
    WINDOWS_10("Windows 10"),
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
