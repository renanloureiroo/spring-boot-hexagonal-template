package com.renanloureiroo.stepbystep.modules.example.infra.http.controllers;

import com.renanloureiroo.stepbystep.modules.example.application.usecases.CreateNoteUseCase;
import com.renanloureiroo.stepbystep.modules.example.application.usecases.GetNoteUseCase;
import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.CreateNoteRequestDTO;
import com.renanloureiroo.stepbystep.modules.example.infra.http.dtos.NoteResponseDTO;
import com.renanloureiroo.stepbystep.modules.example.infra.http.presenters.CreateNotePresenter;
import com.renanloureiroo.stepbystep.modules.example.infra.http.presenters.GetNotePresenter;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/notes")
public class NoteController implements NoteControllerSwagger {

  private final CreateNoteUseCase createNote;
  private final GetNoteUseCase getNote;

  public NoteController(CreateNoteUseCase createNote, GetNoteUseCase getNote) {
    this.createNote = createNote;
    this.getNote = getNote;
  }

  @Override
  @PostMapping
  public ResponseEntity<NoteResponseDTO> create(@Valid @RequestBody CreateNoteRequestDTO request) {
    var response = CreateNotePresenter.present(createNote.execute(request.toInput()));
    var location = currentResourceUri(response.id());
    return ResponseEntity.created(location).body(response);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<NoteResponseDTO> get(@PathVariable String id) {
    var response = GetNotePresenter.present(getNote.execute(new GetNoteUseCase.Input(id)));
    return ResponseEntity.ok(response);
  }

  private static URI currentResourceUri(String id) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri();
  }
}
