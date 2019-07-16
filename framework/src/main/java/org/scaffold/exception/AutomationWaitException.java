package org.scaffold.exception;

public class AutomationWaitException extends RuntimeException {
    private static final long serialVersionUID = 5074263318005328470L;

    public AutomationWaitException(String message) {
        super(message);
    }
}
