package com.renanloureiroo.hexagonal.core.entity;

import com.renanloureiroo.hexagonal.core.event.DomainEvent;
import com.renanloureiroo.hexagonal.core.identity.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AggregateRoot<ID extends Id> extends Entity<ID> {

  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected AggregateRoot(ID id) {
    super(id);
  }

  protected final void registerEvent(DomainEvent event) {
    domainEvents.add(Objects.requireNonNull(event, "event é obrigatório"));
  }

  public final List<DomainEvent> domainEvents() {
    return List.copyOf(domainEvents);
  }

  public final List<DomainEvent> pullDomainEvents() {
    var pending = List.copyOf(domainEvents);
    domainEvents.clear();
    return pending;
  }
}
