package com.zl.cb;

public interface CircuitBreaker {

    /**
     * 获取最近失败计数
     */
    int getFailCount();

    /**
     * 获取熔断器状态
     * @return 熔断器状态
     */
    boolean getState();

    /**
     * 重启熔断器
     */
    void reset();

    /**
     * 调用失败则需要做统计处理
     */
    void fail();

    /**
     * 调用成功计数
     */
    void success();

}
