package com.renanloureiroo.stepbystep.infra.transaction;

import com.renanloureiroo.stepbystep.core.transaction.Transactor;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SpringTransactor implements Transactor {

  private final TransactionTemplate transactionTemplate;

  public SpringTransactor(PlatformTransactionManager transactionManager) {
    transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public <T> T inTransaction(Supplier<T> work) {
    return transactionTemplate.execute(status -> work.get());
  }
}
