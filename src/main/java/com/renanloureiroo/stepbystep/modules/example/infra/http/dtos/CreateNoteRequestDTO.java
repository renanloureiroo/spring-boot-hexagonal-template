package com.renanloureiroo.stepbystep.modules.example.infra.http.dtos;

import com.renanloureiroo.stepbystep.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.stepbystep.modules.example.domain.valueobjects.NoteTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequestDTO(
    @Schema(description = "Título da nota", example = "Minha primeira nota")
        @NotBlank(message = "Título é obrigatório")
        @Size(max = NoteTitle.MAX_LENGTH, message = "Título não pode passar de 120 caracteres")
        String title) {

  public CreateNoteUseCase.Input toInput() {
    return new CreateNoteUseCase.Input(title);
  }
}
