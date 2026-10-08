package com.renanloureiroo.hexagonal.testsupport.factories;

import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import com.renanloureiroo.hexagonal.modules.example.domain.valueobjects.NoteTitle;
import java.time.Instant;

public final class NoteFactory {

  private String title = "Minha primeira nota";
  private Instant createdAt;

  private NoteFactory() {}

  public static NoteFactory aNote() {
    return new NoteFactory();
  }

  public NoteFactory withTitle(String title) {
    this.title = title;
    return this;
  }

  // Com instante fixo a nota é reconstruída, para ordenar de forma determinística nos testes.
  public NoteFactory withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public Note build() {
    if (createdAt != null) {
      return Note.restore(NoteId.generate(), NoteTitle.of(title), createdAt);
    }
    return Note.create(NoteTitle.of(title));
  }

  public Note buildSavedIn(NoteRepository notes) {
    return notes.save(build());
  }

  public CreateNoteUseCase.Input asCreateInput() {
    return new CreateNoteUseCase.Input(title);
  }
}
