package com.renanloureiroo.hexagonal.core.pagination;

public interface PageQuery {
  int page();

  int size();

  default long offset() {
    return (long) page() * size();
  }
}
