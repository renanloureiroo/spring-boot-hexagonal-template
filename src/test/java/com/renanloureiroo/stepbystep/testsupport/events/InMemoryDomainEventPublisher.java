package com.renanloureiroo.stepbystep.testsupport.events;

import com.renanloureiroo.stepbystep.core.event.DomainEvent;
import com.renanloureiroo.stepbystep.core.event.DomainEventPublisher;
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
