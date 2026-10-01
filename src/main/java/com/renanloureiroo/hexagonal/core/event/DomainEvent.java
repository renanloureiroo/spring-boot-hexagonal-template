package com.renanloureiroo.hexagonal.core.event;

import java.time.Instant;

public interface DomainEvent {
  Instant occurredAt();
}
