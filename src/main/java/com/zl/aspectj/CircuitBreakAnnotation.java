package com.zl.aspectj;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要使用熔断器的地方，加上该注解，即可做相应的服务降级处理
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface CircuitBreakAnnotation {

    String fallbackName();

    String className();
}
