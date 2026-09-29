package com.renanloureiroo.stepbystep.modules.example.infra.http.controllers;

import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.CreateNoteRequestDTO;
import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.NoteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface NoteControllerSwagger {

  @Operation(summary = "Cria uma nota")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Nota criada"),
    @ApiResponse(
        responseCode = "400",
        description = "Requisição inválida",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  })
  ResponseEntity<NoteResponseDTO> create(@Valid @RequestBody CreateNoteRequestDTO request);

  @Operation(summary = "Consulta uma nota")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Nota encontrada"),
    @ApiResponse(
        responseCode = "400",
        description = "Identificador inválido",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Nota não encontrada",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  })
  ResponseEntity<NoteResponseDTO> get(@PathVariable String id);
}
