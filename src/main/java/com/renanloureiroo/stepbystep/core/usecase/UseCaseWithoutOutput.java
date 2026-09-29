package com.renanloureiroo.stepbystep.core.usecase;

@FunctionalInterface
public interface UseCaseWithoutOutput<I> {
  void execute(I input);
}
