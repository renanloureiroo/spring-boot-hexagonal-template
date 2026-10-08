package com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.repositories;

import com.renanloureiroo.hexagonal.core.pagination.Page;
import com.renanloureiroo.hexagonal.core.pagination.PageQuery;
import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.mappers.NoteJpaMapper;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class NoteRepositoryJpa implements NoteRepository {

  // Coberta pelo índice notes_created_at_id_idx.
  private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt", "id");

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

  @Override
  public Page<Note> findPage(PageQuery query) {
    var page = repository.findAll(PageRequest.of(query.page(), query.size(), NEWEST_FIRST));
    var items = page.getContent().stream().map(NoteJpaMapper::toDomain).toList();
    return new Page<>(items, page.getTotalElements());
  }
}
