package com.bootgussy.dancecenterservice.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.bootgussy.dancecenterservice.core.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.info("Executing: {}", joinPoint.getSignature());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Completed: {}", joinPoint.getSignature());
            return result;
        } catch (Throwable throwable) {
            LOGGER.error(
                    "Error in method: {} - Exception: ",
                    joinPoint.getSignature(),
                    throwable
            );
            throw throwable;
        }
    }
}
