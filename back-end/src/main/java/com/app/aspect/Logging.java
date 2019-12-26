package com.app.aspect;

import com.app.exceptions.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class Logging {
    private Logger logger = LogManager.getLogger(Logging.class);

    @AfterThrowing(pointcut = "execution(* com.app.controllers.*.*(..))" , throwing = "ex")
    public void AfterThrowingController(JoinPoint joinPoint , CustomException ex) throws Throwable {
        logger.error(joinPoint.getSignature() + "- in method error");
    }

    @AfterThrowing(pointcut = "execution(* com.app.services.*.*(..))" , throwing = "ex")
    public void AfterThrowingService(JoinPoint joinPoint , CustomException ex) throws Throwable {
        logger.error(joinPoint.getSignature() + "- in method error");
    }
}
