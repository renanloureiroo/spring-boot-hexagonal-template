package com.renanloureiroo.stepbystep.modules.example.infra.http.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import com.renanloureiroo.stepbystep.modules.example.infra.database.jpa.repositories.NoteJpaRepository;
import com.renanloureiroo.stepbystep.modules.example.infra.events.NoteCreatedListener;
import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.CreateNoteRequestDTO;
import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.NoteResponseDTO;
import com.renanloureiroo.stepbystep.testsupport.AbstractE2ETest;
import com.renanloureiroo.stepbystep.testsupport.database.DatabaseCleaner;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("/notes")
class NoteControllerE2ETest extends AbstractE2ETest {

  @Autowired RestTestClient client;
  @Autowired NoteJpaRepository notes;
  @Autowired DatabaseCleaner database;
  @Autowired MeterRegistry meters;

  @BeforeEach
  void setUp() {
    database.clean();
  }

  @Test
  void cria_consulta_e_persiste_uma_nota() {
    var eventsBefore = createdNotesCount();

    var result =
        client
            .post()
            .uri("/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new CreateNoteRequestDTO("Minha nota"))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(NoteResponseDTO.class)
            .returnResult();

    var created = result.getResponseBody();

    assertThat(created).isNotNull();
    assertThat(result.getResponseHeaders().getLocation())
        .asString()
        .endsWith("/notes/" + created.id());
    assertThat(notes.findById(created.id())).isPresent();
    assertThat(createdNotesCount()).isEqualTo(eventsBefore + 1);

    client
        .get()
        .uri("/notes/{id}", created.id())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(NoteResponseDTO.class)
        .value(note -> assertThat(note.title()).isEqualTo("Minha nota"));
  }

  @Test
  void rejeita_payload_invalido_sem_persistir() {
    client
        .post()
        .uri("/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new CreateNoteRequestDTO(" "))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("request.invalid")
        .jsonPath("$.errors.title")
        .isEqualTo("Título é obrigatório");

    assertThat(notes.count()).isZero();
  }

  @Test
  void rejeita_json_malformado_sem_persistir() {
    client
        .post()
        .uri("/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"title\":}")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("request.invalid");

    assertThat(notes.count()).isZero();
  }

  @Test
  void devolve_404_para_nota_inexistente() {
    client
        .get()
        .uri("/notes/{id}", NoteId.generate().value())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("note.not_found");
  }

  @Test
  void devolve_400_para_identificador_invalido() {
    client
        .get()
        .uri("/notes/invalido")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("note.id_invalid");
  }

  private double createdNotesCount() {
    return meters.counter(NoteCreatedListener.METRIC_NAME).count();
  }
}
