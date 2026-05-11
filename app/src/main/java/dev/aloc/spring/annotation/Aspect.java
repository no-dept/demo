package dev.aloc.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AOP 적용 설정(Advice)을 담고 있는 클래스임을 나타내는 애너테이션입니다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component // Aspect 클래스도 스프링 빈으로 등록되어야 합니다.
public @interface Aspect {
}