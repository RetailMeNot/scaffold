package io.github.kgress.scaffold.models.enums;

/**
 * A simple enum to differentiate the many browsers that Selenium supports.
 */
public enum BrowserType {

    Firefox("Firefox"),
    Chrome("Chrome"),
    Safari("Safari"),
    InternetExplorer("InternetExplorer"),
    Edge("Edge"),
    Opera("Opera");

    private String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserName() {
        return browserName;
    }
}
