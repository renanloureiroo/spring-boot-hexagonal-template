package com.renanloureiroo.stepbystep.modules.example.domain.valueobjects;

import com.renanloureiroo.stepbystep.core.error.DomainException;
import com.renanloureiroo.stepbystep.core.error.ErrorType;

public record NoteTitle(String value) {

  public static final int MAX_LENGTH = 120;
  private static final String INVALID_CODE = "note.title_invalid";

  public NoteTitle {
    if (value == null || value.isBlank()) {
      throw new DomainException(ErrorType.VALIDATION, INVALID_CODE, "Título é obrigatório");
    }
    if (value.length() > MAX_LENGTH) {
      throw new DomainException(
          ErrorType.VALIDATION,
          INVALID_CODE,
          "Título não pode passar de " + MAX_LENGTH + " caracteres");
    }
  }

  public static NoteTitle of(String value) {
    return new NoteTitle(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
