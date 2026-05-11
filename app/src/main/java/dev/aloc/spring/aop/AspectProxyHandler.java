package dev.aloc.spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 다이내믹 프록시의 동작 로직(InvocationHandler)을 구현한 클래스입니다.
 * 프록시 객체의 메서드가 호출될 때마다, 이 클래스의 invoke() 메서드가 대신 실행됩니다.
 */
public class AspectProxyHandler implements InvocationHandler {

  private final Object target; // 프록시가 감싸고 있는 원본 객체

  public AspectProxyHandler(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    // 1. 핵심 기능 실행 전 (Before)
    // (나중에 여기에 @Around 어드바이스를 실행하는 로직이 들어갈 예정입니다)
    System.out.println(">>> [AOP] " + target.getClass().getSimpleName()
        + "." + method.getName() + "() 실행 시작");

    // 2. 원본 객체의 핵심 기능(메서드) 실행
    Object result = method.invoke(target, args);

    // 3. 핵심 기능 실행 후 (After)
    System.out.println("bye");
    System.out.println("<<< [AOP] " + target.getClass().getSimpleName()
        + "." + method.getName() + "() 실행 종료");

    return result;
  }
}