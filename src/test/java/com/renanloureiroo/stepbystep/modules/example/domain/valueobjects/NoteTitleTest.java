package com.renanloureiroo.stepbystep.modules.example.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.renanloureiroo.stepbystep.core.error.DomainException;
import com.renanloureiroo.stepbystep.core.error.ErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NoteTitleTest {

  @ParameterizedTest
  @ValueSource(strings = {"Nota", "Minha primeira nota", "a"})
  void aceita_titulo_valido(String valid) {
    assertThat(NoteTitle.of(valid).value()).isEqualTo(valid);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void rejeita_titulo_ausente(String invalid) {
    assertThatThrownBy(() -> NoteTitle.of(invalid))
        .isInstanceOf(DomainException.class)
        .satisfies(
            error -> {
              var domainError = (DomainException) error;
              assertThat(domainError.type()).isEqualTo(ErrorType.VALIDATION);
              assertThat(domainError.code()).isEqualTo("note.title_invalid");
            });
  }

  @Test
  void respeita_o_limite_maximo() {
    assertThat(NoteTitle.of("a".repeat(120)).value()).hasSize(120);

    assertThatThrownBy(() -> NoteTitle.of("a".repeat(121)))
        .isInstanceOf(DomainException.class)
        .extracting(error -> ((DomainException) error).code())
        .isEqualTo("note.title_invalid");
  }

  @Test
  void usa_o_valor_como_representacao_textual() {
    assertThat(NoteTitle.of("Nota")).hasToString("Nota");
  }
}
