package com.example.playgame.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {
    private final HttpServletRequest request;

    public ControllerLoggingAspect(final HttpServletRequest request) {
        this.request = request;
    }

    @Around("execution(* com.example.playgame.controllers.*.*(..))")
    public Object controllerLoggingMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String requestUrl = request.getRequestURI();
        String httpMethod = request.getMethod();
        Object[] args = joinPoint.getArgs();

        log.info("Incoming request: {} {} with method: {}, parameters: {}", httpMethod, requestUrl, methodName, args);

        try {
            Object result = joinPoint.proceed(args);

            log.info("Request to {} {} completed with result {}", httpMethod, requestUrl, result);

            return result;
        } catch (Exception e) {
            log.error("Request to {} {} failed with exception: {}", httpMethod, requestUrl, e.getMessage());
            throw e;
        }
    }
}
