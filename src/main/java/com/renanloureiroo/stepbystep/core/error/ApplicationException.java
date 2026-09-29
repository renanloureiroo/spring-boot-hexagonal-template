package com.renanloureiroo.stepbystep.core.error;

import java.util.Objects;

public abstract class ApplicationException extends RuntimeException {

  private final ErrorType type;
  private final String code;

  protected ApplicationException(ErrorType type, String code, String message) {
    this(type, code, message, null);
  }

  protected ApplicationException(ErrorType type, String code, String message, Throwable cause) {
    super(message, cause);
    this.type = Objects.requireNonNull(type, "type é obrigatório");
    this.code = Objects.requireNonNull(code, "code é obrigatório");
  }

  public ErrorType type() {
    return type;
  }

  public String code() {
    return code;
  }
}
