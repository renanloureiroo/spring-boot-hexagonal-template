package com.renanloureiroo.hexagonal.modules.example.application.errors;

import com.renanloureiroo.hexagonal.core.error.NotFoundException;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;

public final class NoteNotFoundException extends NotFoundException {

  private static final String CODE = "note.not_found";

  public NoteNotFoundException(NoteId id) {
    super(CODE, "Nota não encontrada: " + id);
  }
}
