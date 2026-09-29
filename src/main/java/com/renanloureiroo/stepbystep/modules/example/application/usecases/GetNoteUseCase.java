package com.renanloureiroo.stepbystep.modules.example.application.usecases;

import com.renanloureiroo.stepbystep.core.usecase.UseCase;
import com.renanloureiroo.stepbystep.modules.example.application.errors.NoteNotFoundException;
import com.renanloureiroo.stepbystep.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.Note;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import java.time.Instant;

public final class GetNoteUseCase implements UseCase<GetNoteUseCase.Input, GetNoteUseCase.Output> {

  private final NoteRepository notes;

  public GetNoteUseCase(NoteRepository notes) {
    this.notes = notes;
  }

  @Override
  public Output execute(Input input) {
    var id = NoteId.of(input.id());
    var note = notes.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    return Output.from(note);
  }

  public record Input(String id) {}

  public record Output(String id, String title, Instant createdAt) {

    private static Output from(Note note) {
      return new Output(note.id().value(), note.title().value(), note.createdAt());
    }
  }
}
