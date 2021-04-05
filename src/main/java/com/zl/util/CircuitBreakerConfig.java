package com.zl.util;

import com.zl.cb.AbstractCircuitBreaker;
import com.zl.cb.CircuitBreaker;
import com.zl.cb.LocalCircuiteBreaker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CircuitBreakerConfig {

    private static CircuitBreaker circuitBreaker;

    /**
     * 获取熔断器实例
     * @return 熔断器实例
     */
    public static CircuitBreaker getInstance() throws IOException {
        if (circuitBreaker == null) {
            circuitBreaker = createInstance();
        }
        return circuitBreaker;
    }

    /**
     * 创建一个熔断器实例
     * @return
     * @throws IOException
     */
    private static synchronized CircuitBreaker createInstance() throws IOException {
        Properties properties = new Properties();
        InputStream in = AbstractCircuitBreaker.class.getClassLoader()
                .getResourceAsStream("com/zl/cb/circuitebreak.properties");
        properties.load(in);
        Integer maxRequestWindow = Integer.valueOf(properties.getProperty("maxRequestWindow"));
        Integer maxFailWindow = Integer.valueOf(properties.getProperty("maxFailWindow"));
        Long thresholdIdleTimeForWindow = Long.valueOf(properties.getProperty("thresholdIdleTimeForWindow"));
        return new LocalCircuiteBreaker(thresholdIdleTimeForWindow, maxRequestWindow, maxFailWindow);
    }

}
