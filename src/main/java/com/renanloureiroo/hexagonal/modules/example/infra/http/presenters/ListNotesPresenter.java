package com.renanloureiroo.hexagonal.modules.example.infra.http.presenters;

import com.renanloureiroo.hexagonal.modules.example.application.usecases.ListNotesUseCase;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NotePageResponseDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NoteResponseDTO;

public final class ListNotesPresenter {

  private ListNotesPresenter() {}

  public static NotePageResponseDTO present(ListNotesUseCase.Output output) {
    var items =
        output.items().stream()
            .map(item -> new NoteResponseDTO(item.id(), item.title(), item.createdAt()))
            .toList();
    return new NotePageResponseDTO(items, output.total());
  }
}
