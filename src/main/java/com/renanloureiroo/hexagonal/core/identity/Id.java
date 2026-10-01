package com.renanloureiroo.hexagonal.core.identity;

import com.renanloureiroo.hexagonal.core.error.DomainException;
import com.renanloureiroo.hexagonal.core.error.ErrorType;
import java.util.UUID;

public abstract class Id {

  private static final String INVALID_CODE = "id.invalid";

  private final String value;

  protected Id(String value) {
    if (value == null || value.isBlank()) {
      throw new DomainException(
          ErrorType.VALIDATION, INVALID_CODE, "Identificador não pode ser vazio");
    }
    this.value = value;
  }

  protected static String newValue() {
    return UUID.randomUUID().toString();
  }

  protected static boolean isUuid(String value) {
    try {
      return UUID.fromString(value).toString().equalsIgnoreCase(value);
    } catch (IllegalArgumentException notUuid) {
      return false;
    }
  }

  public String value() {
    return value;
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return value.equals(((Id) other).value);
  }

  @Override
  public final int hashCode() {
    return 31 * getClass().hashCode() + value.hashCode();
  }

  @Override
  public final String toString() {
    return value;
  }
}
