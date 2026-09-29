package com.renanloureiroo.stepbystep.modules.example.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.renanloureiroo.stepbystep.modules.example.application.errors.NoteNotFoundException;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import com.renanloureiroo.stepbystep.testsupport.factories.NoteFactory;
import com.renanloureiroo.stepbystep.testsupport.repositories.InMemoryNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetNoteUseCaseTest {

  private InMemoryNoteRepository notes;
  private GetNoteUseCase sut;

  @BeforeEach
  void setUp() {
    notes = new InMemoryNoteRepository();
    sut = new GetNoteUseCase(notes);
  }

  @Test
  void devolve_a_nota_existente() {
    var note = NoteFactory.aNote().buildSavedIn(notes);

    var output = sut.execute(new GetNoteUseCase.Input(note.id().value()));

    assertThat(output.id()).isEqualTo(note.id().value());
    assertThat(output.title()).isEqualTo(note.title().value());
  }

  @Test
  void rejeita_nota_inexistente() {
    var id = NoteId.generate();

    assertThatThrownBy(() -> sut.execute(new GetNoteUseCase.Input(id.value())))
        .isInstanceOf(NoteNotFoundException.class)
        .extracting(error -> ((NoteNotFoundException) error).code())
        .isEqualTo("note.not_found");
  }
}
