package com.renanloureiroo.stepbystep.testsupport.repositories;

import com.renanloureiroo.stepbystep.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.Note;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryNoteRepository implements NoteRepository {

  private final Map<NoteId, Note> notes = new LinkedHashMap<>();

  @Override
  public Note save(Note note) {
    notes.put(note.id(), note);
    return note;
  }

  @Override
  public Optional<Note> findById(NoteId id) {
    return Optional.ofNullable(notes.get(id));
  }

  public List<Note> findAll() {
    return List.copyOf(notes.values());
  }
}
