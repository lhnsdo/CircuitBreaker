#### 1:熔断器配置
```
maxRequestWindow    // 最大请求窗口长度，统计窗口范围总数
maxFailWindow       // 在统计窗口范围内，最多允许失败的次数
thresholdIdleTimeForWindow  // 熔断器开启后多长时间恢复正常
```
#### 2:使用
```
1、在fallback包中加入降级方法
2、App中是一个测试demo；
    @CircuitBreakAnnotation(fallbackName = "fallBack", className = "com.zl.fallback.FallBackDemo")
    public void test() throws AccessException {}

```
1）服务降级方法名：fallBack（和fallBack包中的降级方法名相同）
2）服务降级类名：com.zl.fallback.FallBackDemo（对应服务降级中的类）

