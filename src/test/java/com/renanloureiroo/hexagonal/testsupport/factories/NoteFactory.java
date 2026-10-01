package com.renanloureiroo.hexagonal.testsupport.factories;

import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.valueobjects.NoteTitle;

public final class NoteFactory {

  private String title = "Minha primeira nota";

  private NoteFactory() {}

  public static NoteFactory aNote() {
    return new NoteFactory();
  }

  public NoteFactory withTitle(String title) {
    this.title = title;
    return this;
  }

  public Note build() {
    return Note.create(NoteTitle.of(title));
  }

  public Note buildSavedIn(NoteRepository notes) {
    return notes.save(build());
  }

  public CreateNoteUseCase.Input asCreateInput() {
    return new CreateNoteUseCase.Input(title);
  }
}
