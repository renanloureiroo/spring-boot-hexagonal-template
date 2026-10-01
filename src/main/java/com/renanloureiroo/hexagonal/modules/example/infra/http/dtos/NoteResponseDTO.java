package com.renanloureiroo.hexagonal.modules.example.infra.http.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record NoteResponseDTO(
    @Schema(example = "f59dd6bb-e086-4fe9-a382-a00f8796d260") String id,
    @Schema(example = "Minha primeira nota") String title,
    @Schema(example = "2026-09-26T12:00:00Z") Instant createdAt) {}
