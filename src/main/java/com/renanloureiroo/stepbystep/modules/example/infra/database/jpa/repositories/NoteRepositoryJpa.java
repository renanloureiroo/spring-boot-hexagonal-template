package com.renanloureiroo.stepbystep.modules.example.infra.database.jpa.repositories;

import com.renanloureiroo.stepbystep.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.Note;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import com.renanloureiroo.stepbystep.modules.example.infra.database.jpa.mappers.NoteJpaMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class NoteRepositoryJpa implements NoteRepository {

  private final NoteJpaRepository repository;

  public NoteRepositoryJpa(NoteJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Note save(Note note) {
    return NoteJpaMapper.toDomain(repository.save(NoteJpaMapper.toJpa(note)));
  }

  @Override
  public Optional<Note> findById(NoteId id) {
    return repository.findById(id.value()).map(NoteJpaMapper::toDomain);
  }
}
