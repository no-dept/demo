package dev.aloc.spring.testcomponents;

/**
 * [1] 인터페이스 정의
 * JDK Dynamic Proxy가 이 인터페이스를 기반으로 가짜 객체(Proxy)를 만듭니다.
 */
public interface MyService {
  String getHelloMessage();
}