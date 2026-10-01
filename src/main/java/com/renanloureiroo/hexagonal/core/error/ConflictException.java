package com.renanloureiroo.hexagonal.core.error;

public class ConflictException extends ApplicationException {

  public ConflictException(String code, String message) {
    super(ErrorType.CONFLICT, code, message);
  }

  public ConflictException(String code, String message, Throwable cause) {
    super(ErrorType.CONFLICT, code, message, cause);
  }
}
