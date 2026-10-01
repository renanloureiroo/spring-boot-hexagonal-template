package com.renanloureiroo.hexagonal.modules.example.domain.events;

import com.renanloureiroo.hexagonal.core.event.DomainEvent;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.NoteId;
import java.time.Instant;

public record NoteCreated(NoteId noteId, Instant occurredAt) implements DomainEvent {}
