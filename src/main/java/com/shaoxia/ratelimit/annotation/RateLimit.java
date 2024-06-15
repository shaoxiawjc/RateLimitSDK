package com.shaoxia.ratelimit.annotation;

import org.redisson.api.RateIntervalUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default ""; // 标识前缀key
    int timeWindow() default 1; // 频控时间范围
    RateIntervalUnit timeUnit() default RateIntervalUnit.MINUTES; // 频控时间单位
    int maxRequests() default 5; // 单位频控时间范围内最大访问次数
}