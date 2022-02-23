package io.github.kgress.scaffold.exception;

public class ComponentException extends RuntimeException {

  public ComponentException(String message) {
    super(message);
  }

  public ComponentException(String message, Throwable cause) {
    super(message, cause);
  }

  public ComponentException(Throwable cause) {
    super(cause);
  }
}
