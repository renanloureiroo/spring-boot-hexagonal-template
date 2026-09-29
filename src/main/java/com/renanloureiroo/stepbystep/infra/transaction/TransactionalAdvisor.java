package com.renanloureiroo.stepbystep.infra.transaction;

import com.renanloureiroo.stepbystep.core.transaction.Transactional;
import com.renanloureiroo.stepbystep.core.transaction.Transactor;
import java.lang.reflect.UndeclaredThrowableException;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.stereotype.Component;

@Component
public class TransactionalAdvisor extends AbstractPointcutAdvisor {

  private static final Pointcut POINTCUT =
      new ComposablePointcut(AnnotationMatchingPointcut.forClassAnnotation(Transactional.class))
          .union(AnnotationMatchingPointcut.forMethodAnnotation(Transactional.class));

  private final transient Advice advice;

  public TransactionalAdvisor(Transactor transactor) {
    advice = (MethodInterceptor) invocation -> transactor.inTransaction(() -> proceed(invocation));
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
