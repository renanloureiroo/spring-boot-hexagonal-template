package com.renanloureiroo.stepbystep.core.pagination;

import java.util.List;
import java.util.function.Function;

public record Page<T>(List<T> items, long total) {

  public Page {
    items = List.copyOf(items);
  }

  public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
    return new Page<>(items.stream().<R>map(mapper).toList(), total);
  }

  public int totalPages(int size) {
    return size == 0 ? 0 : (int) Math.ceil((double) total / size);
  }
}
