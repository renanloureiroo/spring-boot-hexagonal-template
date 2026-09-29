package com.renanloureiroo.stepbystep.testsupport.transaction;

import com.renanloureiroo.stepbystep.core.transaction.Transactor;
import java.util.function.Supplier;

public class DirectTransactor implements Transactor {

  private int invocations;

  @Override
  public <T> T inTransaction(Supplier<T> work) {
    invocations++;
    return work.get();
  }

  public int invocations() {
    return invocations;
  }
}
