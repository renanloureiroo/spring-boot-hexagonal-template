package com.renanloureiroo.stepbystep.modules.example.infra.http.presenters;

import com.renanloureiroo.stepbystep.modules.example.application.usecases.GetNoteUseCase;
import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.NoteResponseDTO;

public final class GetNotePresenter {

  private GetNotePresenter() {}

  public static NoteResponseDTO present(GetNoteUseCase.Output output) {
    return new NoteResponseDTO(output.id(), output.title(), output.createdAt());
  }
}
