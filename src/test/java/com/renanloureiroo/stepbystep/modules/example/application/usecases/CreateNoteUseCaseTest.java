package com.renanloureiroo.stepbystep.modules.example.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import com.renanloureiroo.stepbystep.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.stepbystep.modules.example.domain.events.NoteCreated;
import com.renanloureiroo.stepbystep.testsupport.events.InMemoryDomainEventPublisher;
import com.renanloureiroo.stepbystep.testsupport.factories.NoteFactory;
import com.renanloureiroo.stepbystep.testsupport.repositories.InMemoryNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateNoteUseCaseTest {

  private NoteRepository notes;
  private InMemoryDomainEventPublisher events;
  private CreateNoteUseCase sut;

  @BeforeEach
  void setUp() {
    notes = new InMemoryNoteRepository();
    events = new InMemoryDomainEventPublisher();
    sut = new CreateNoteUseCase(notes, events);
  }

  @Test
  void cria_e_persiste_uma_nota() {
    var output = sut.execute(NoteFactory.aNote().asCreateInput());

    var saved =
        notes.findById(
            com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId.of(output.id()));

    assertThat(saved).isPresent();
    assertThat(saved.orElseThrow().title().value()).isEqualTo("Minha primeira nota");
    assertThat(output.createdAt()).isNotNull();
    assertThat(events.published())
        .singleElement()
        .isInstanceOfSatisfying(
            NoteCreated.class, event -> assertThat(event.noteId().value()).isEqualTo(output.id()));
  }
}
