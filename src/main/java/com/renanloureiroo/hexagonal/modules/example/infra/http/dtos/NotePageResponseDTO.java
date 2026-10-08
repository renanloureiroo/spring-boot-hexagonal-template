package com.renanloureiroo.hexagonal.modules.example.infra.http.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NotePageResponseDTO(
    List<NoteResponseDTO> items,
    @Schema(description = "Total de notas, em todas as páginas", example = "42") long total) {}
