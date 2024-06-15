package com.shaoxia.ratelimit.aop;

import com.shaoxia.ratelimit.annotation.RateLimit;
import com.shaoxia.ratelimit.exception.RateLimitException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String ip = getIpAddress();
        if (Objects.isNull(ip)){
            throw new RateLimitException("ip信息异常");
        }
        String key = String.format("%s:%s", rateLimit.key(), ip);
        int maxRequests = rateLimit.maxRequests();
        int timeWindow = rateLimit.timeWindow();
        RateIntervalUnit timeUnit = rateLimit.timeUnit();
        boolean allowed = tryAcquire(key, maxRequests, timeWindow, timeUnit);
        if (!allowed) {
            throw new RateLimitException("服务器繁忙");
        }
        return joinPoint.proceed();
    }

    // 使用令牌桶算法
    private boolean tryAcquire(String key, int rate, int rateInterval, RateIntervalUnit rateIntervalUnit) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.PER_CLIENT, rate, rateInterval, rateIntervalUnit);
        return rateLimiter.tryAcquire();
    }

    private String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(",") >0 ){
            String[] parts = ip.split(",");
            for (String part : parts) {
                if (!part.isEmpty() && !"unknown".equalsIgnoreCase(part)) {
                    ip = part.trim();
                    break;
                }
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
