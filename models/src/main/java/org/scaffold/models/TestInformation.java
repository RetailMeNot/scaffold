package io.github.kgress.scaffold.models;

import java.util.List;

public class TestInformation {

    private String screenShotUrl;
    private List<String> testSteps;
    private Throwable exception;
    private String runHost;

    public String getScreenShotUrl() {
        return screenShotUrl;
    }

    public List<String> getTestSteps() {
        return testSteps;
    }

    public Throwable getException() {
        return exception;
    }

    public String getRunHost() {
        return runHost;
    }

    public TestInformation screenShotUrl(String screenShotUrl) {
        this.screenShotUrl = screenShotUrl;
        return this;
    }

    public TestInformation testSteps(List<String> testSteps) {
        this.testSteps = testSteps;
        return this;
    }

    public TestInformation exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    public TestInformation runHost(String runHost) {
        this.runHost = runHost;
        return this;
    }
}
