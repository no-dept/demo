package dev.aloc.spring.exception;

/**
 * 새로운 Bean을 만드는 도중 문제가 생겼을 경우 예외를 발생시킨다.
 */
public class BeanCreationException extends RuntimeException {
    
    // 전달받은 메시지와 에러 발생 원인을 함께 표시한다.
    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
