package com.info.aspect;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    @Around("@annotation(com.info.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String key = pjp.getSignature().toLongString();
        RateLimiter limiter = limiters.computeIfAbsent(key,
                k -> RateLimiter.create(1.0));
        if (!limiter.tryAcquire(1, TimeUnit.SECONDS)) {
            throw new RuntimeException("System busy, try again later");
        }
        return pjp.proceed();
    }
}