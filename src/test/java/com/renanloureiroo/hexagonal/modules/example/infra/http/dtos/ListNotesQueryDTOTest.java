package com.renanloureiroo.hexagonal.modules.example.infra.http.dtos;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ListNotesQueryDTOTest {

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
  void usa_a_primeira_pagina_com_vinte_itens_por_padrao() {
    var query = new ListNotesQueryDTO(null, null);

    assertThat(violationsOf(query)).isEmpty();
    assertThat(query.toInput().page()).isZero();
    assertThat(query.toInput().size()).isEqualTo(20);
  }

  @Test
  void converte_para_input() {
    var input = new ListNotesQueryDTO(2, 50).toInput();

    assertThat(input.page()).isEqualTo(2);
    assertThat(input.size()).isEqualTo(50);
  }

  @Test
  void respeita_o_limite_inferior_da_pagina() {
    assertThat(violationsOf(new ListNotesQueryDTO(0, 20))).isEmpty();
    assertThat(violationsOf(new ListNotesQueryDTO(-1, 20)))
        .containsExactly(Map.entry("page", "Página deve ser maior ou igual a 0"));
  }

  @Test
  void respeita_os_limites_do_tamanho() {
    assertThat(violationsOf(new ListNotesQueryDTO(0, 1))).isEmpty();
    assertThat(violationsOf(new ListNotesQueryDTO(0, 100))).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 101})
  void rejeita_tamanho_fora_dos_limites(int size) {
    assertThat(violationsOf(new ListNotesQueryDTO(0, size)))
        .containsExactly(Map.entry("size", "Tamanho deve estar entre 1 e 100"));
  }

  @Test
  void reporta_todos_os_campos_invalidos_juntos() {
    assertThat(violationsOf(new ListNotesQueryDTO(-1, 0)))
        .containsOnlyKeys("page", "size");
  }

  private static Map<String, String> violationsOf(ListNotesQueryDTO query) {
    return validator.validate(query).stream()
        .collect(
            Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (first, second) -> first));
  }
}
