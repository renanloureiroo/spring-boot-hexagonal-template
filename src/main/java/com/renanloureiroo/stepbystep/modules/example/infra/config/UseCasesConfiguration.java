package com.renanloureiroo.stepbystep.modules.example.infra.config;

import com.renanloureiroo.stepbystep.core.event.DomainEventPublisher;
import com.renanloureiroo.stepbystep.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.stepbystep.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.stepbystep.modules.example.application.usecases.GetNoteUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UseCasesConfiguration {

  @Bean
  CreateNoteUseCase createNoteUseCase(NoteRepository notes, DomainEventPublisher events) {
    return new CreateNoteUseCase(notes, events);
  }

  @Bean
  GetNoteUseCase getNoteUseCase(NoteRepository notes) {
    return new GetNoteUseCase(notes);
  }
}
