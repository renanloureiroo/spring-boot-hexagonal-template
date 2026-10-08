package com.renanloureiroo.hexagonal.modules.example.infra.config;

import com.renanloureiroo.hexagonal.core.event.DomainEventPublisher;
import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.hexagonal.modules.example.application.usecases.GetNoteUseCase;
import com.renanloureiroo.hexagonal.modules.example.application.usecases.ListNotesUseCase;
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

  @Bean
  ListNotesUseCase listNotesUseCase(NoteRepository notes) {
    return new ListNotesUseCase(notes);
  }
}
