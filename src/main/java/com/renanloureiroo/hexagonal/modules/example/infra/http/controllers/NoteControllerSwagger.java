package com.renanloureiroo.hexagonal.modules.example.infra.http.controllers;

import com.renanloureiroo.hexagonal.infra.http.dtos.ProblemDetailDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.CreateNoteRequestDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.ListNotesQueryDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NotePageResponseDTO;
import com.renanloureiroo.hexagonal.modules.example.infra.http.dtos.NoteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

// Contrato OpenAPI fora do controller. Cada erro anuncia o corpo RFC 9457 real que a borda
// produz, com um exemplo por status.
@Tag(name = "notes")
public interface NoteControllerSwagger {

  String PROBLEM_JSON = "application/problem+json";

  String CREATE_INVALID =
      """
      {
        "type": "about:blank",
        "title": "Bad Request",
        "status": 400,
        "detail": "Requisição inválida",
        "instance": "/api/notes",
        "code": "request.invalid",
        "traceId": "4bf92f3577b34da6a3ce929d0e0e4736",
        "errors": { "title": "Título é obrigatório" }
      }
      """;

  String LIST_INVALID =
      """
      {
        "type": "about:blank",
        "title": "Bad Request",
        "status": 400,
        "detail": "Requisição inválida",
        "instance": "/api/notes",
        "code": "request.invalid",
        "traceId": "4bf92f3577b34da6a3ce929d0e0e4736",
        "errors": {
          "page": "Página deve ser maior ou igual a 0",
          "size": "Tamanho deve estar entre 1 e 100"
        }
      }
      """;

  String ID_INVALID =
      """
      {
        "type": "about:blank",
        "title": "Bad Request",
        "status": 400,
        "detail": "Identificador de nota inválido",
        "instance": "/api/notes/invalido",
        "code": "note.id_invalid",
        "traceId": "4bf92f3577b34da6a3ce929d0e0e4736"
      }
      """;

  String NOT_FOUND =
      """
      {
        "type": "about:blank",
        "title": "Not Found",
        "status": 404,
        "detail": "Nota não encontrada: f59dd6bb-e086-4fe9-a382-a00f8796d260",
        "instance": "/api/notes/f59dd6bb-e086-4fe9-a382-a00f8796d260",
        "code": "note.not_found",
        "traceId": "4bf92f3577b34da6a3ce929d0e0e4736"
      }
      """;

  @Operation(summary = "Lista as notas, mais recentes primeiro")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de notas"),
    @ApiResponse(
        responseCode = "400",
        description = "Paginação inválida",
        content =
            @Content(
                mediaType = PROBLEM_JSON,
                schema = @Schema(implementation = ProblemDetailDTO.class),
                examples = @ExampleObject(name = "request.invalid", value = LIST_INVALID)))
  })
  ResponseEntity<NotePageResponseDTO> list(@ParameterObject @Valid ListNotesQueryDTO query);

  @Operation(summary = "Cria uma nota")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Nota criada"),
    @ApiResponse(
        responseCode = "400",
        description = "Requisição inválida",
        content =
            @Content(
                mediaType = PROBLEM_JSON,
                schema = @Schema(implementation = ProblemDetailDTO.class),
                examples = @ExampleObject(name = "request.invalid", value = CREATE_INVALID)))
  })
  ResponseEntity<NoteResponseDTO> create(@Valid @RequestBody CreateNoteRequestDTO request);

  @Operation(summary = "Consulta uma nota")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Nota encontrada"),
    @ApiResponse(
        responseCode = "400",
        description = "Identificador inválido",
        content =
            @Content(
                mediaType = PROBLEM_JSON,
                schema = @Schema(implementation = ProblemDetailDTO.class),
                examples = @ExampleObject(name = "note.id_invalid", value = ID_INVALID))),
    @ApiResponse(
        responseCode = "404",
        description = "Nota não encontrada",
        content =
            @Content(
                mediaType = PROBLEM_JSON,
                schema = @Schema(implementation = ProblemDetailDTO.class),
                examples = @ExampleObject(name = "note.not_found", value = NOT_FOUND)))
  })
  ResponseEntity<NoteResponseDTO> get(@PathVariable String id);
}
