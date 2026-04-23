package com.autotrack.inventoryservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.autotrack.inventoryservice.controller.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.autotrack.inventoryservice.service.*.*(..))")
    public void serviceMethods() {}

    @Around("controllerMethods()")
    public Object logControllerAccess(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();

        log.info("[API REQUEST] {}.{}() | Args: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        Object result = pjp.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        log.info("[API RESPONSE] {}.{}() | Executed in: {} ms", className, methodName, executionTime);

        return result;
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logServiceExceptions(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("[ERROR] {}.{}() | Reason: {}", className, methodName, exception.getMessage());
    }
}