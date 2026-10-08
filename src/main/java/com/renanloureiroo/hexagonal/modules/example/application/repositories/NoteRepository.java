package com.renanloureiroo.hexagonal.modules.example.application.repositories;

import com.renanloureiroo.hexagonal.core.pagination.Page;
import com.renanloureiroo.hexagonal.core.pagination.PageQuery;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import java.util.Optional;

public interface NoteRepository {
  Note save(Note note);

  Optional<Note> findById(NoteId id);

  // Mais recentes primeiro; o identificador desempata notas criadas no mesmo instante.
  Page<Note> findPage(PageQuery query);
}
