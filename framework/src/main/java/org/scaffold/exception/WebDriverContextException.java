package org.scaffold.exception;

public class WebDriverContextException extends RuntimeException {
    private static final long serialVersionUID = 5553101862609859L;

    public WebDriverContextException(String message) {
        super(message);
    }

    public WebDriverContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
