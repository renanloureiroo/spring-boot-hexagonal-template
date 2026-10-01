package com.renanloureiroo.hexagonal.core.usecase;

@FunctionalInterface
public interface UseCaseWithoutInput<O> {
  O execute();
}
