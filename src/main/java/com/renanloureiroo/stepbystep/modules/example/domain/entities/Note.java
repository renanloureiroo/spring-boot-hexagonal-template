package com.renanloureiroo.stepbystep.modules.example.domain.entities;

import com.renanloureiroo.stepbystep.core.entity.AggregateRoot;
import com.renanloureiroo.stepbystep.modules.example.domain.events.NoteCreated;
import com.renanloureiroo.stepbystep.modules.example.domain.valueobjects.NoteTitle;
import java.time.Instant;
import java.util.Objects;

public final class Note extends AggregateRoot<NoteId> {

  private final NoteTitle title;
  private final Instant createdAt;

  private Note(NoteId id, NoteTitle title, Instant createdAt) {
    super(id);
    this.title = Objects.requireNonNull(title, "title é obrigatório");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt é obrigatório");
  }

  public static Note create(NoteTitle title) {
    var note = new Note(NoteId.generate(), title, Instant.now());
    note.registerEvent(new NoteCreated(note.id(), note.createdAt()));
    return note;
  }

  public static Note restore(NoteId id, NoteTitle title, Instant createdAt) {
    return new Note(id, title, createdAt);
  }

  public NoteTitle title() {
    return title;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
