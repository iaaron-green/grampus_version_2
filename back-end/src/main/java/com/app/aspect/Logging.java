package com.app.aspect;

import com.app.util.CustomException;
import org.apache.logging.log4j.LogManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class Logging {
    private Logger logger = LogManager.getLogger(Logging.class);


    @Before("execution(* com.app.controllers.*.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info(joinPoint.getSignature() +" - is start");
    }

    @After("execution(* com.app.controllers.*.*(..))")
    public void logAfterController(JoinPoint joinPoint) {
        logger.info(joinPoint.getSignature() +" - successfully completed");
    }

    @AfterThrowing(pointcut = "execution(* com.app.controllers.*.*(..))" , throwing = "ex")
    public void AfterThrowingController(JoinPoint joinPoint , CustomException ex) throws Throwable {
        logger.error(joinPoint.getSignature() + "- in method error");
    }

    @Before("execution(* com.app.services.*.*(..))")
    public void logBeforeService(JoinPoint joinPoint) {
        logger.info(joinPoint.getSignature() +" - is start");
    }

    @After("execution(* com.app.services.*.*(..))")
    public void logAfterService(JoinPoint joinPoint) {
        logger.info(joinPoint.getSignature() +" - successfully completed");
    }
    @AfterThrowing(pointcut = "execution(* com.app.services.*.*(..))" , throwing = "ex")
    public void AfterThrowingService(JoinPoint joinPoint , CustomException ex) throws Throwable {
        logger.error(joinPoint.getSignature() + "- in method error");
    }
}
