package com.renanloureiroo.hexagonal.core.error;

public class DomainException extends ApplicationException {

  public DomainException(String code, String message) {
    super(ErrorType.BUSINESS_RULE, code, message);
  }

  public DomainException(ErrorType type, String code, String message) {
    super(type, code, message);
  }
}
