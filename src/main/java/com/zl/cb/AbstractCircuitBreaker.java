package com.zl.cb;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AbstractCircuitBreaker implements CircuitBreaker {

    // 熔斷器的状态
    private volatile boolean state = false;
    // 最近请求中成功和失败的状态
    private volatile ConcurrentLinkedQueue<Boolean> queue = new ConcurrentLinkedQueue<Boolean>();
    // 距离上次读取状态是否有新的请求服务出现,如果没出现则直接获取失败率即可，
    // 否则需要重新计算队列中的失败率
    private volatile boolean isModify = false;
    // 熔断窗口时间（默认熔断30分钟）
    private long thresholdIdleTimeForWindow = 1800;
    // 关闭时间
    private volatile long closeTime = 0;
    // 统计窗口长度，即如果没有设置该值，默认统计最新的一百次请求
    private int maxRequestWindow = 100;
    // 窗口期内的失败触发熔断阈值
    private int maxFailWindow = 20;
    // 最近统计的失败次数
    private volatile int failCount = 0;

    @Override
    public int getFailCount() {
        // 显示如果距离上次重新计数时，统计队列，则需要重新计数
        if (isModify) {
            countFailNumber();
        }
        return failCount;
    }

    @Override
    public boolean getState() {
        Long currentTime = System.currentTimeMillis() / 1000;
        if (state) {
            // 如果断路器是开着的，则查看熔断延迟时间
            if (closeTime < currentTime) {
                // 如果关闭时间小于当前时间，则重置断路器
                reset();
            }
        } else {
            // 如果段利器是关着的，则判断当前的状态是否达到短路状态
            if (getFailCount() >= maxFailWindow) {
                state = true;
                closeTime = currentTime + thresholdIdleTimeForWindow;
            }
        }
        return state;
    }


    @Override
    public void reset() {
        // 重置断路器
        synchronized (this) {
            queue = new ConcurrentLinkedQueue<Boolean>();
            isModify = false;
            failCount = 0;
            state = false;
        }
    }

    @Override
    public void fail() {
        queue.offer(false);
        // 如果队列中的结果超过窗口长度，则移除最旧的数据
        while (queue.size() > maxRequestWindow) {
            queue.poll();
        }
        isModify = true;
        getState();
    }

    @Override
    public void success() {
        queue.offer(true);
    }


    /**
     * 从队列中获取失败次数
     */
    private void countFailNumber() {
        int size = queue.size();
        if (size != 0) {
            int currentFailCount = 0;
            for (Boolean result : queue) {
                if (!result) {
                    currentFailCount++;
                }
            }
            synchronized(this) {
                failCount = currentFailCount;
                isModify = false;
            }
        }
    }



    protected void setThresholdIdleTimeForWindow(long thresholdIdleTimeForWindow) {
        this.thresholdIdleTimeForWindow = thresholdIdleTimeForWindow;
    }

    protected void setMaxRequestWindow(int maxRequestWindow) {
        this.maxRequestWindow = maxRequestWindow;
    }

    protected void setMaxFailWindow(int maxFailWindow) {
        this.maxFailWindow = maxFailWindow;
    }
}








