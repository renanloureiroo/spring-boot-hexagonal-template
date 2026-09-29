package com.renanloureiroo.stepbystep.modules.example.application.repositories;

import com.renanloureiroo.stepbystep.modules.example.domain.entities.Note;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import java.util.Optional;

public interface NoteRepository {
  Note save(Note note);

  Optional<Note> findById(NoteId id);
}
