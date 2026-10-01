package com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.mappers;

import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import com.renanloureiroo.hexagonal.modules.example.domain.valueobjects.NoteTitle;
import com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.entities.NoteJpaEntity;

public final class NoteJpaMapper {

  private NoteJpaMapper() {}

  public static NoteJpaEntity toJpa(Note note) {
    return new NoteJpaEntity(note.id().value(), note.title().value(), note.createdAt());
  }

  public static Note toDomain(NoteJpaEntity note) {
    return Note.restore(
        NoteId.of(note.getId()), NoteTitle.of(note.getTitle()), note.getCreatedAt());
  }
}
