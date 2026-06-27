package com.info.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("@annotation(com.info.annotation.Log)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();

        log.info("[AOP] {} start, args: {}", method, pjp.getArgs());

        Object result = pjp.proceed();

        long elapsed = System.currentTimeMillis() - start;
        log.info("[AOP] {} done in {}ms, result: {}", method, elapsed, result);

        return result;
    }
}