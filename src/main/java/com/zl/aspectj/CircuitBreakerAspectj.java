package com.zl.aspectj;

import com.zl.cb.CircuitBreaker;
import com.zl.util.CircuitBreakerConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class CircuitBreakerAspectj {

    private static final Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();

    @Around("execution(* *(..)) && @annotation(CircuitBreakAnnotation)")
    public Object around(ProceedingJoinPoint point, CircuitBreakAnnotation CircuitBreakAnnotation) throws Throwable {
        // 获取熔断器
        CircuitBreaker circuitBreaker = CircuitBreakerConfig.getInstance();
        //获取fallback方法
        String className = CircuitBreakAnnotation.className();
        Class<?> clazz = Class.forName(className);
        Object[] params = point.getArgs();
        Method method = getMethod(params, CircuitBreakAnnotation.fallbackName(), clazz);
        Object newInstance = clazz.newInstance();
        // 如果已经熔断，则调用fallback里的方法
        if (circuitBreaker.getState()) {
            //调用fallback方法
            if (params.length == 0) {
                return method.invoke(newInstance);
            } else {
                return method.invoke(newInstance, params);
            }
        }
        // 没有熔断，走原有逻辑
        try {
            Object proceed = point.proceed();
            circuitBreaker.success();
            return proceed;
        } catch (Throwable throwable) {
            // 调用失败计数
            circuitBreaker.fail();
            //调用fallback方法
            if (params.length == 0) {
                return method.invoke(newInstance);
            } else {
                return method.invoke(newInstance, params);
            }
        }
    }



    private static Method getMethod(Object[] params, String fallbackName, Class clazz) throws NoSuchMethodException, ClassNotFoundException {
        String methodKey = clazz.getName() + fallbackName;
        Method method = methodMap.get(methodKey);
        if (method == null) {
            Class[] paramClasss = new Class[params.length];
            if (params.length == 0) {
                method = clazz.getMethod(fallbackName);
            } else {
                for (int i = 0; i < params.length; i++) {
                    paramClasss[i] = params[i].getClass();
                }
                method = clazz.getMethod(fallbackName, paramClasss);
                if (method == null) {
                    Method[] methods = clazz.getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Method method1 = methods[i];
                        //如果方法名称和方法参数个数相同
                        if (method1.getName().equals(fallbackName)
                                && method1.getParameterTypes().length == paramClasss.length) {
                            method = method1;
                        }
                    }
                }
            }
            methodMap.put(methodKey, method);
        }
        return method;
    }


}
