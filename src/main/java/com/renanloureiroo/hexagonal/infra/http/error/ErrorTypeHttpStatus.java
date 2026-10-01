package com.renanloureiroo.hexagonal.infra.http.error;

import com.renanloureiroo.hexagonal.core.error.ErrorType;
import org.springframework.http.HttpStatus;

final class ErrorTypeHttpStatus {

  private ErrorTypeHttpStatus() {}

  static HttpStatus of(ErrorType type) {
    return switch (type) {
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case CONFLICT -> HttpStatus.CONFLICT;
      case VALIDATION -> HttpStatus.BAD_REQUEST;
      case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
      case FORBIDDEN -> HttpStatus.FORBIDDEN;
      case BUSINESS_RULE -> HttpStatus.UNPROCESSABLE_CONTENT;
    };
  }
}
