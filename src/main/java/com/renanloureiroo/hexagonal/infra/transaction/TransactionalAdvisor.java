package com.renanloureiroo.hexagonal.infra.transaction;

import com.renanloureiroo.hexagonal.core.transaction.Transactional;
import com.renanloureiroo.hexagonal.core.transaction.Transactor;
import java.lang.reflect.UndeclaredThrowableException;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class TransactionalAdvisor extends AbstractPointcutAdvisor {

  private static final Pointcut POINTCUT =
      new ComposablePointcut(AnnotationMatchingPointcut.forClassAnnotation(Transactional.class))
          .union(AnnotationMatchingPointcut.forMethodAnnotation(Transactional.class));

  private final transient Advice advice;

  // O Transactor é resolvido na chamada, não na construção. O auto-proxy cria advisors durante o
  // registro dos BeanPostProcessors; resolver aqui puxaria transactionManager e DataSource cedo
  // demais, antes do post-processor que instrumenta o JDBC para observabilidade.
  public TransactionalAdvisor(ObjectProvider<Transactor> transactor) {
    advice =
        (MethodInterceptor)
            invocation -> transactor.getObject().inTransaction(() -> proceed(invocation));
  }

  @Override
  public Pointcut getPointcut() {
    return POINTCUT;
  }

  @Override
  public Advice getAdvice() {
    return advice;
  }

  private static Object proceed(MethodInvocation invocation) {
    try {
      return invocation.proceed();
    } catch (RuntimeException | Error propagated) {
      throw propagated;
    } catch (Throwable checked) {
      throw new UndeclaredThrowableException(checked);
    }
  }
}
