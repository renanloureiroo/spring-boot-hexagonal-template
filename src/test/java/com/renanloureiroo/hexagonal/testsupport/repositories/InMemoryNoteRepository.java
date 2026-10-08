package com.renanloureiroo.hexagonal.testsupport.repositories;

import com.renanloureiroo.hexagonal.core.pagination.Page;
import com.renanloureiroo.hexagonal.core.pagination.PageQuery;
import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryNoteRepository implements NoteRepository {

  // Mesma ordem do adaptador JPA: createdAt e id decrescentes.
  private static final Comparator<Note> NEWEST_FIRST =
      Comparator.comparing(Note::createdAt)
          .thenComparing(note -> note.id().value())
          .reversed();

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

  @Override
  public Page<Note> findPage(PageQuery query) {
    var items =
        notes.values().stream()
            .sorted(NEWEST_FIRST)
            .skip(query.offset())
            .limit(query.size())
            .toList();
    return new Page<>(items, notes.size());
  }

  public List<Note> findAll() {
    return List.copyOf(notes.values());
  }
}
