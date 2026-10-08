package com.renanloureiroo.hexagonal.modules.example.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import com.renanloureiroo.hexagonal.testsupport.factories.NoteFactory;
import com.renanloureiroo.hexagonal.testsupport.repositories.InMemoryNoteRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListNotesUseCaseTest {

  private InMemoryNoteRepository notes;
  private ListNotesUseCase sut;

  @BeforeEach
  void setUp() {
    notes = new InMemoryNoteRepository();
    sut = new ListNotesUseCase(notes);
  }

  @Test
  void devolve_as_notas_mais_recentes_primeiro_com_o_total() {
    var older = noteAt("2026-01-01T00:00:00Z", "Antiga");
    var newer = noteAt("2026-01-03T00:00:00Z", "Recente");
    var middle = noteAt("2026-01-02T00:00:00Z", "Meio");

    var output = sut.execute(new ListNotesUseCase.Input(0, 20));

    assertThat(output.items())
        .extracting(ListNotesUseCase.Item::id)
        .containsExactly(newer.id().value(), middle.id().value(), older.id().value());
    assertThat(output.items().getFirst().title()).isEqualTo("Recente");
    assertThat(output.items().getFirst().createdAt()).isEqualTo(newer.createdAt());
    assertThat(output.total()).isEqualTo(3);
  }

  @Test
  void desempata_notas_do_mesmo_instante_pelo_identificador_decrescente() {
    var first = noteAt("2026-01-01T00:00:00Z", "Uma");
    var second = noteAt("2026-01-01T00:00:00Z", "Outra");
    var expected =
        first.id().value().compareTo(second.id().value()) > 0
            ? new String[] {first.id().value(), second.id().value()}
            : new String[] {second.id().value(), first.id().value()};

    var output = sut.execute(new ListNotesUseCase.Input(0, 20));

    assertThat(output.items()).extracting(ListNotesUseCase.Item::id).containsExactly(expected);
  }

  @Test
  void devolve_a_pagina_pedida_sem_perder_o_total() {
    noteAt("2026-01-01T00:00:00Z", "Primeira");
    var second = noteAt("2026-01-02T00:00:00Z", "Segunda");
    noteAt("2026-01-03T00:00:00Z", "Terceira");

    var output = sut.execute(new ListNotesUseCase.Input(1, 1));

    assertThat(output.items())
        .extracting(ListNotesUseCase.Item::id)
        .containsExactly(second.id().value());
    assertThat(output.total()).isEqualTo(3);
  }

  @Test
  void devolve_pagina_vazia_alem_do_fim() {
    noteAt("2026-01-01T00:00:00Z", "Única");

    var output = sut.execute(new ListNotesUseCase.Input(5, 20));

    assertThat(output.items()).isEmpty();
    assertThat(output.total()).isEqualTo(1);
  }

  @Test
  void devolve_lista_vazia_sem_notas() {
    var output = sut.execute(new ListNotesUseCase.Input(0, 20));

    assertThat(output.items()).isEmpty();
    assertThat(output.total()).isZero();
  }

  private Note noteAt(String instant, String title) {
    return NoteFactory.aNote()
        .withTitle(title)
        .withCreatedAt(Instant.parse(instant))
        .buildSavedIn(notes);
  }
}
