package io.github.kgress.scaffold.models.enums;

/**
 * A simple enum to differentiate the many browsers that Selenium supports.
 */
public enum BrowserType {

    FIREFOX("Firefox"),
    CHROME("Chrome"),
    SAFARI("Safari"),
    INTERNET_EXPLORER("Internet Explorer"),
    EDGE("Edge"),
    OPERA("Opera");

    private String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserName() {
        return browserName;
    }
}
