package com.renanloureiroo.hexagonal.core.usecase;

@FunctionalInterface
public interface UseCase<I, O> {
  O execute(I input);
}
