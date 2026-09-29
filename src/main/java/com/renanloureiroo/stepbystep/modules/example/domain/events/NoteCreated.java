package com.renanloureiroo.stepbystep.modules.example.domain.events;

import com.renanloureiroo.stepbystep.core.event.DomainEvent;
import com.renanloureiroo.stepbystep.modules.example.domain.entities.NoteId;
import java.time.Instant;

public record NoteCreated(NoteId noteId, Instant occurredAt) implements DomainEvent {}
