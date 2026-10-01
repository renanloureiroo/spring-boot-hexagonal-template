package com.renanloureiroo.hexagonal.modules.example.infra.http.presenters;

import com.renanloureiroo.hexagonal.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NoteResponseDTO;

public final class CreateNotePresenter {

  private CreateNotePresenter() {}

  public static NoteResponseDTO present(CreateNoteUseCase.Output output) {
    return new NoteResponseDTO(output.id(), output.title(), output.createdAt());
  }
}
