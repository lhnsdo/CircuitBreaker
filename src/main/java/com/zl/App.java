package com.zl;

import com.zl.aspectj.CircuitBreakAnnotation;
import org.aspectj.lang.annotation.Aspect;

import java.rmi.AccessException;

@Aspect
public class App {

    private static final String LOCK = "lock";

    public static void main(String[] args) throws AccessException {
        new App().testDemo();
    }

    private void testDemo() throws AccessException {
        for (int i = 0; i < 200; i++) {
            test();
            if (i == 30) {
                try {
                    Thread.sleep(4 * 1000);
                    System.out.println(" ========= ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @CircuitBreakAnnotation(fallbackName = "fallBack", className = "com.zl.fallback.FallBackDemo")
    public void test() throws AccessException {
        System.out.println("normal");
        if (Math.random() > 0.5) {
            throw new AccessException("exception");
        }
    }


}
