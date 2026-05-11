package dev.aloc.spring.testcomponents;

/**
 * [1] 리포지토리 인터페이스 정의
 * 서비스 계층이 바라보는 규약(Contract)입니다.
 */
public interface MyRepository {
  // MyServiceImpl에서 호출하려고 했던 그 메서드
  String getData();
}