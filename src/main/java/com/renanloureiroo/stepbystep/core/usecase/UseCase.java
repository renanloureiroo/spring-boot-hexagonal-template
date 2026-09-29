package com.renanloureiroo.stepbystep.core.usecase;

@FunctionalInterface
public interface UseCase<I, O> {
  O execute(I input);
}
