package com.renanloureiroo.hexagonal.core.usecase;

@FunctionalInterface
public interface UseCaseWithoutOutput<I> {
  void execute(I input);
}
