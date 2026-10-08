package com.renanloureiroo.hexagonal.modules.example.infra.http.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.entities.NoteJpaEntity;
import com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.repositories.NoteJpaRepository;
import com.renanloureiroo.hexagonal.modules.example.infra.events.NoteCreatedListener;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.CreateNoteRequestDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NotePageResponseDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NoteResponseDTO;
import com.renanloureiroo.hexagonal.testsupport.AbstractE2ETest;
import com.renanloureiroo.hexagonal.testsupport.database.DatabaseCleaner;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

  @Test
  void lista_as_notas_mais_recentes_primeiro_paginadas() {
    var older = persistNote("Antiga", "2026-01-01T00:00:00Z");
    var newer = persistNote("Recente", "2026-01-03T00:00:00Z");
    var middle = persistNote("Meio", "2026-01-02T00:00:00Z");

    var firstPage = listNotes("/notes?page=0&size=2");
    var secondPage = listNotes("/notes?page=1&size=2");

    assertThat(firstPage.total()).isEqualTo(3);
    assertThat(firstPage.items())
        .extracting(NoteResponseDTO::id)
        .containsExactly(newer.getId(), middle.getId());
    assertThat(firstPage.items().getFirst())
        .isEqualTo(
            new NoteResponseDTO(newer.getId(), "Recente", Instant.parse("2026-01-03T00:00:00Z")));
    assertThat(secondPage.total()).isEqualTo(3);
    assertThat(secondPage.items()).extracting(NoteResponseDTO::id).containsExactly(older.getId());
  }

  @Test
  void lista_com_a_paginacao_padrao() {
    persistNote("Única", "2026-01-01T00:00:00Z");

    client
        .get()
        .uri("/notes")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.total")
        .isEqualTo(1)
        .jsonPath("$.items[0].title")
        .isEqualTo("Única")
        .jsonPath("$.items[0].createdAt")
        .isEqualTo("2026-01-01T00:00:00Z");
  }

  @ParameterizedTest
  @CsvSource({
    "page=-1, page, Página deve ser maior ou igual a 0",
    "size=0, size, Tamanho deve estar entre 1 e 100",
    "size=101, size, Tamanho deve estar entre 1 e 100",
    "page=abc, page, Página deve ser maior ou igual a 0"
  })
  void rejeita_paginacao_invalida(String query, String field, String message) {
    client
        .get()
        .uri("/notes?" + query)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("request.invalid")
        .jsonPath("$.errors." + field)
        .isEqualTo(message)
        .jsonPath("$.detail")
        .isEqualTo("Requisição inválida");
  }

  @Test
  void publica_os_status_e_os_exemplos_de_erro_no_openapi() {
    client
        .get()
        .uri("/v3/api-docs")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.paths['/notes'].get.responses.length()")
        .isEqualTo(2)
        .jsonPath("$.paths['/notes'].get.responses['200']")
        .exists()
        .jsonPath("$.paths['/notes'].get.responses['400']")
        .exists()
        .jsonPath("$.paths['/notes'].post.responses.length()")
        .isEqualTo(2)
        .jsonPath("$.paths['/notes/{id}'].get.responses.length()")
        .isEqualTo(3)
        .jsonPath(
            "$.paths['/notes/{id}'].get.responses['404'].content['application/problem+json']"
                + ".examples['note.not_found'].value.code")
        .isEqualTo("note.not_found")
        .jsonPath(
            "$.paths['/notes'].post.responses['400'].content['application/problem+json']"
                + ".examples['request.invalid'].value.errors.title")
        .isEqualTo("Título é obrigatório")
        .jsonPath("$.components.schemas.ProblemDetail.properties.code")
        .exists();
  }

  private NotePageResponseDTO listNotes(String uri) {
    return client
        .get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(NotePageResponseDTO.class)
        .returnResult()
        .getResponseBody();
  }

  private NoteJpaEntity persistNote(String title, String createdAt) {
    return notes.save(
        new NoteJpaEntity(NoteId.generate().value(), title, Instant.parse(createdAt)));
  }

  private double createdNotesCount() {
    return meters.counter(NoteCreatedListener.METRIC_NAME).count();
  }
}
