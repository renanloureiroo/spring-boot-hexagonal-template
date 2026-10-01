package com.renanloureiroo.hexagonal.modules.example.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import com.renanloureiroo.hexagonal.modules.example.domain.events.NoteCreated;
import com.renanloureiroo.hexagonal.modules.example.domain.valueobjects.NoteTitle;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class NoteTest {

  @Test
  void registra_evento_quando_nasce() {
    var note = Note.create(NoteTitle.of("Minha nota"));

    assertThat(note.domainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            NoteCreated.class,
            event -> {
              assertThat(event.noteId()).isEqualTo(note.id());
              assertThat(event.occurredAt()).isEqualTo(note.createdAt());
            });
  }

  @Test
  void restore_nao_registra_evento() {
    var note =
        Note.restore(
            NoteId.generate(), NoteTitle.of("Minha nota"), Instant.parse("2026-01-01T00:00:00Z"));

    assertThat(note.domainEvents()).isEmpty();
  }

  @Test
  void pull_devolve_e_limpa_eventos_pendentes() {
    var note = Note.create(NoteTitle.of("Minha nota"));

    assertThat(note.pullDomainEvents()).hasSize(1);
    assertThat(note.domainEvents()).isEmpty();
  }
}
