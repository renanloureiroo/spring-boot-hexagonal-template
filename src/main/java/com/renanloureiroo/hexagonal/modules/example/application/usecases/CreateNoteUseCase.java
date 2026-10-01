package com.renanloureiroo.hexagonal.modules.example.application.usecases;

import com.renanloureiroo.hexagonal.core.event.DomainEventPublisher;
import com.renanloureiroo.hexagonal.core.transaction.Transactional;
import com.renanloureiroo.hexagonal.core.usecase.UseCase;
import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.valueobjects.NoteTitle;
import java.time.Instant;

public class CreateNoteUseCase
    implements UseCase<CreateNoteUseCase.Input, CreateNoteUseCase.Output> {

  private final NoteRepository notes;
  private final DomainEventPublisher events;

  public CreateNoteUseCase(NoteRepository notes, DomainEventPublisher events) {
    this.notes = notes;
    this.events = events;
  }

  @Override
  @Transactional
  public Output execute(Input input) {
    var note = Note.create(NoteTitle.of(input.title()));
    var saved = notes.save(note);
    events.publish(note.pullDomainEvents());
    return Output.from(saved);
  }

  public record Input(String title) {}

  public record Output(String id, String title, Instant createdAt) {

    private static Output from(Note note) {
      return new Output(note.id().value(), note.title().value(), note.createdAt());
    }
  }
}
