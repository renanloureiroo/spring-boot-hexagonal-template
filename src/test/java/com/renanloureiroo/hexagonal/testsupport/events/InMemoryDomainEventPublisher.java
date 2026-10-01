package com.renanloureiroo.hexagonal.testsupport.events;

import com.renanloureiroo.hexagonal.core.event.DomainEvent;
import com.renanloureiroo.hexagonal.core.event.DomainEventPublisher;
import java.util.ArrayList;
import java.util.List;

public class InMemoryDomainEventPublisher implements DomainEventPublisher {

  private final List<DomainEvent> published = new ArrayList<>();

  @Override
  public void publish(List<DomainEvent> events) {
    published.addAll(events);
  }

  public List<DomainEvent> published() {
    return List.copyOf(published);
  }
}
