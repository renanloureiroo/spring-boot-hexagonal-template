package com.renanloureiroo.hexagonal.infra.http.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

// Contrato RFC 9457 publicado no OpenAPI para toda resposta de erro. Só documenta: quem produz
// a resposta é o ApiExceptionHandler, com o ProblemDetail do Spring.
@Schema(name = "ProblemDetail", description = "Erro no formato RFC 9457 com code estável")
public record ProblemDetailDTO(
    @Schema(
            description = "Omitido quando about:blank",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String type,
    @Schema(example = "Not Found") String title,
    @Schema(example = "404") int status,
    @Schema(example = "Nota não encontrada: f59dd6bb-e086-4fe9-a382-a00f8796d260") String detail,
    @Schema(example = "/api/notes/f59dd6bb-e086-4fe9-a382-a00f8796d260") String instance,
    @Schema(description = "Código estável do erro", example = "note.not_found") String code,
    @Schema(
            description = "Trace da requisição, quando disponível",
            example = "4bf92f3577b34da6a3ce929d0e0e4736",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String traceId,
    @Schema(
            description = "Erros por campo, presentes quando code é request.invalid",
            example = "{\"title\": \"Título é obrigatório\"}",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Map<String, String> errors) {}
