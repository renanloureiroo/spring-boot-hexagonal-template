package com.renanloureiroo.stepbystep.core.usecase;

@FunctionalInterface
public interface UseCaseWithoutInput<O> {
  O execute();
}
