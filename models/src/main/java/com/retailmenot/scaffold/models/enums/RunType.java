package com.retailmenot.scaffold.models.enums;

/**
 * A simple enum to differentiate the many run types that Selenium can handle.
 */
public enum RunType {
    UNIT("UNIT"),
    LOCAL("LOCAL"),
    GRID("GRID"),
    SAUCE("SAUCE");

    private final String runType;

    RunType(String runType) {
        this.runType = runType;
    }

    public String getRunType() {
        return runType;
    }
}
