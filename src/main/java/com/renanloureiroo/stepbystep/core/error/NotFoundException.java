package com.renanloureiroo.stepbystep.core.error;

public class NotFoundException extends ApplicationException {

  public NotFoundException(String code, String message) {
    super(ErrorType.NOT_FOUND, code, message);
  }

  public NotFoundException(String code, String message, Throwable cause) {
    super(ErrorType.NOT_FOUND, code, message, cause);
  }
}
