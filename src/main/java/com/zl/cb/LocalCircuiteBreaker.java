package com.zl.cb;

import com.zl.cb.AbstractCircuitBreaker;

public class LocalCircuiteBreaker extends AbstractCircuitBreaker {

    public LocalCircuiteBreaker(long thresholdIdleTimeForWindow, int maxRequestWindow, int maxFailWindow) {
        this.setThresholdIdleTimeForWindow(thresholdIdleTimeForWindow);
        this.setMaxRequestWindow(maxRequestWindow);
        this.setMaxFailWindow(maxFailWindow);
    }

    public LocalCircuiteBreaker() {
    }
}
