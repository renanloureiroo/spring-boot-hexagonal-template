package com.renanloureiroo.hexagonal.modules.example.infra.http.dtos;

import com.renanloureiroo.hexagonal.modules.example.application.usecases.ListNotesUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// Parâmetros de query de GET /notes. Ausência vira o padrão; o valor recebido é validado como veio.
public record ListNotesQueryDTO(
    @Schema(description = "Página, começando em 0", example = "0", defaultValue = "0")
        @Min(value = 0, message = PAGE_MESSAGE)
        Integer page,
    @Schema(description = "Itens por página", example = "20", defaultValue = "20")
        @Min(value = 1, message = SIZE_MESSAGE)
        @Max(value = MAX_SIZE, message = SIZE_MESSAGE)
        Integer size) {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;
  public static final int MAX_SIZE = 100;
  public static final String PAGE_MESSAGE = "Página deve ser maior ou igual a 0";
  public static final String SIZE_MESSAGE = "Tamanho deve estar entre 1 e 100";

  public ListNotesUseCase.Input toInput() {
    return new ListNotesUseCase.Input(
        page == null ? DEFAULT_PAGE : page, size == null ? DEFAULT_SIZE : size);
  }
}
