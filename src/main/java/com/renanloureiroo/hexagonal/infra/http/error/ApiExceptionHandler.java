package com.renanloureiroo.hexagonal.infra.http.error;

import com.renanloureiroo.hexagonal.core.error.ApplicationException;
import io.micrometer.tracing.Tracer;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String VALIDATION_CODE = "request.invalid";
  private static final String UNEXPECTED_CODE = "internal.unexpected";
  private static final String INVALID_FIELD_MESSAGE = "Valor inválido";

  private final Optional<Tracer> tracer;

  public ApiExceptionHandler(Optional<Tracer> tracer) {
    this.tracer = tracer;
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ProblemDetail> handleApplicationException(
      ApplicationException error, WebRequest request) {
    var status = ErrorTypeHttpStatus.of(error.type());
    var problem = problemOf(status, error.getMessage(), error.code(), request);

    log.info("Erro de aplicação [{}] {}", error.code(), error.getMessage());
    return ResponseEntity.status(status).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpectedException(
      Exception error, WebRequest request) {
    var problem =
        problemOf(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro inesperado ao processar a requisição",
            UNEXPECTED_CODE,
            request);

    log.error("Erro inesperado ao processar a requisição", error);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException error,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var problem = problemOf(status, "Requisição inválida", VALIDATION_CODE, request);
    problem.setProperty("errors", fieldErrorsOf(error, request.getLocale()));
    return ResponseEntity.status(status).body(problem);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException error,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var problem = problemOf(status, "Corpo da requisição malformado", VALIDATION_CODE, request);
    return ResponseEntity.status(status).body(problem);
  }

  private ProblemDetail problemOf(
      HttpStatusCode status, String detail, String code, WebRequest request) {
    var problem = ProblemDetail.forStatusAndDetail(status, detail);
    if (status instanceof HttpStatus resolved) {
      problem.setTitle(resolved.getReasonPhrase());
    }
    problem.setProperty("code", code);
    tracer
        .map(Tracer::currentSpan)
        .map(span -> span.context().traceId())
        .ifPresent(traceId -> problem.setProperty("traceId", traceId));

    var description = request.getDescription(false);
    if (description.startsWith("uri=")) {
      problem.setInstance(URI.create(description.substring(4)));
    }
    return problem;
  }

  // Erro de conversão (page=abc) usa a mensagem de messages.properties (typeMismatch.<campo>),
  // para não vazar a exceção de conversão; constraint usa a própria mensagem.
  private Map<String, String> fieldErrorsOf(MethodArgumentNotValidException error, Locale locale) {
    return error.getBindingResult().getFieldErrors().stream()
        .collect(
            Collectors.toMap(
                FieldError::getField,
                fieldError -> messageOf(fieldError, locale),
                (first, second) -> first,
                LinkedHashMap::new));
  }

  private String messageOf(FieldError fieldError, Locale locale) {
    var messages = getMessageSource();
    if (fieldError.isBindingFailure() && messages != null) {
      var resolvable =
          new DefaultMessageSourceResolvable(fieldError.getCodes(), INVALID_FIELD_MESSAGE);
      return messages.getMessage(resolvable, locale);
    }
    if (fieldError.isBindingFailure() || fieldError.getDefaultMessage() == null) {
      return INVALID_FIELD_MESSAGE;
    }
    return fieldError.getDefaultMessage();
  }
}
