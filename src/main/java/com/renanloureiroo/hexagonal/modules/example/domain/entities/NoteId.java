package com.renanloureiroo.hexagonal.modules.example.domain.entities;

import com.renanloureiroo.hexagonal.core.error.DomainException;
import com.renanloureiroo.hexagonal.core.error.ErrorType;
import com.renanloureiroo.hexagonal.core.identity.Id;

public final class NoteId extends Id {

  private static final String INVALID_CODE = "note.id_invalid";

  private NoteId(String value) {
    super(value);
    if (!isUuid(value)) {
      throw new DomainException(
          ErrorType.VALIDATION, INVALID_CODE, "Identificador de nota inválido");
    }
  }

  public static NoteId generate() {
    return new NoteId(newValue());
  }

  public static NoteId of(String value) {
    return new NoteId(value);
  }
}
