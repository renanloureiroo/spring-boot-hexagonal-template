package com.renanloureiroo.stepbystep.modules.example.application.errors;

import com.renanloureiroo.stepbystep.core.error.NotFoundException;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;

public final class NoteNotFoundException extends NotFoundException {

  private static final String CODE = "note.not_found";

  public NoteNotFoundException(NoteId id) {
    super(CODE, "Nota não encontrada: " + id);
  }
}
