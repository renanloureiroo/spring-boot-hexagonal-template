package com.renanloureiroo.hexagonal.modules.example.application.usecases;

import com.renanloureiroo.hexagonal.core.pagination.PageQuery;
import com.renanloureiroo.hexagonal.core.usecase.UseCase;
import com.renanloureiroo.hexagonal.modules.example.application.repositories.NoteRepository;
import com.renanloureiroo.hexagonal.modules.example.domain.entities.Note;
import java.time.Instant;
import java.util.List;

public final class ListNotesUseCase
    implements UseCase<ListNotesUseCase.Input, ListNotesUseCase.Output> {

  private final NoteRepository notes;

  public ListNotesUseCase(NoteRepository notes) {
    this.notes = notes;
  }

  @Override
  public Output execute(Input input) {
    var page = notes.findPage(input).map(Item::from);
    return new Output(page.items(), page.total());
  }

  public record Input(int page, int size) implements PageQuery {}

  public record Output(List<Item> items, long total) {

    public Output {
      items = List.copyOf(items);
    }
  }

  public record Item(String id, String title, Instant createdAt) {

    private static Item from(Note note) {
      return new Item(note.id().value(), note.title().value(), note.createdAt());
    }
  }
}
