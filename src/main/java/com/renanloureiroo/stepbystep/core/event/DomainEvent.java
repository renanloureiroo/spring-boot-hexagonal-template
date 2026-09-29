package com.renanloureiroo.stepbystep.core.event;

import java.time.Instant;

public interface DomainEvent {
  Instant occurredAt();
}
