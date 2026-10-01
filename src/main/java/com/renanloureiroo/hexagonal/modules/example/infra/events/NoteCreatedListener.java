package com.renanloureiroo.hexagonal.modules.example.infra.events;

import com.renanloureiroo.hexagonal.modules.example.domain.events.NoteCreated;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class NoteCreatedListener {

  public static final String METRIC_NAME = "example.notes.created";

  private final Counter createdNotes;

  public NoteCreatedListener(MeterRegistry registry) {
    createdNotes = Counter.builder(METRIC_NAME).register(registry);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(NoteCreated event) {
    createdNotes.increment();
    log.info("Evento NoteCreated processado [{}]", event.noteId());
  }
}
