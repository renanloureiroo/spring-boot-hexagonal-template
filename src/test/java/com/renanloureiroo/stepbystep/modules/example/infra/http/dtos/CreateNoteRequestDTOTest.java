package com.renanloureiroo.stepbystep.modules.example.infra.http.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CreateNoteRequestDTOTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void startValidator() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void closeValidator() {
    factory.close();
  }

  @Test
  void aceita_payload_valido_e_converte_para_input() {
    var request = new CreateNoteRequestDTO("Minha nota");

    assertThat(violationsOf(request)).isEmpty();
    assertThat(request.toInput().title()).isEqualTo("Minha nota");
  }

  @Test
  void rejeita_titulo_ausente_com_a_mensagem_do_dominio() {
    assertThat(violationsOf(new CreateNoteRequestDTO("")))
        .containsEntry("title", "Título é obrigatório");
  }

  @Test
  void rejeita_titulo_longo_com_a_mensagem_do_dominio() {
    assertThat(violationsOf(new CreateNoteRequestDTO("a".repeat(121))))
        .containsEntry("title", "Título não pode passar de 120 caracteres");
  }

  private static Map<String, String> violationsOf(CreateNoteRequestDTO request) {
    return validator.validate(request).stream()
        .collect(
            Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (first, second) -> first));
  }
}
