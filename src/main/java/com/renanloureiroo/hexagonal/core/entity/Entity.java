package com.renanloureiroo.hexagonal.core.entity;

import com.renanloureiroo.hexagonal.core.error.DomainException;
import com.renanloureiroo.hexagonal.core.error.ErrorType;
import com.renanloureiroo.hexagonal.core.identity.Id;
import java.util.Objects;

public abstract class Entity<ID extends Id> {

  private static final String MISSING_ID_CODE = "entity.id_required";

  private final ID id;

  protected Entity(ID id) {
    if (id == null) {
      throw new DomainException(
          ErrorType.VALIDATION, MISSING_ID_CODE, "Entidade precisa de um identificador");
    }
    this.id = id;
  }

  public ID id() {
    return id;
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return id.equals(((Entity<?>) other).id);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(getClass(), id);
  }
}
