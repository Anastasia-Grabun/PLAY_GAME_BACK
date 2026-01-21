package com.example.playgame.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.playgame.service.*.*(..)) || execution(* com.example.playgame.repository.*.*(..))")
    public Object logMethods(final ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Method: {} with args: {}", methodName, args);

        try {
            Object result = joinPoint.proceed(args);
            log.info("Method: {} with result: {}", methodName, result);
            return result;
        } catch (Exception e) {
            log.error("Method: {} with exception: {}", methodName, e.getMessage());
            throw e;
        }
    }
}

