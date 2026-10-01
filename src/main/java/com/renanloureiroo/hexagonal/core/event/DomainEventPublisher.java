package com.renanloureiroo.hexagonal.core.event;

import java.util.List;

public interface DomainEventPublisher {
  void publish(List<DomainEvent> events);
}
