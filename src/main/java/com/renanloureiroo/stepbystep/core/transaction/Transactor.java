package com.renanloureiroo.stepbystep.core.transaction;

import java.util.function.Supplier;

public interface Transactor {

  <T> T inTransaction(Supplier<T> work);

  default void runInTransaction(Runnable work) {
    inTransaction(
        () -> {
          work.run();
          return null;
        });
  }
}
