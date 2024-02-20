package com.hanghae.newsfeed.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect // AOP 클래스 선언 : 부가 기능을 주입하는 클래스
@Component
public class DebuggingAspect {

    // 대상 메소드 선택
    @Pointcut("execution(* com.hanghae.newsfeed.auth.service.*.*(..))")
    private void authCut() {}
    @Pointcut("execution(* com.hanghae.newsfeed.user.service.*.*(..))")
    private void userCut() {}
    @Pointcut("execution(* com.hanghae.newsfeed.admin.service.*.*(..))")
    private void adminCut() {}
    @Pointcut("execution(* com.hanghae.newsfeed.post.service.*.*(..))")
    private void postCut() {}
    @Pointcut("execution(* com.hanghae.newsfeed.comment.service.*.*(..))")
    private void commentCut() {}
    @Pointcut("execution(* com.hanghae.newsfeed.follow.service.*.*(..))")
    private void followCut() {}
    @Pointcut("execution(* com.hanghae.newsfeed.like.service.*.*(..))")
    private void likeCut() {}

    // 실행 시점 설정 : cut()의 대상이 수행되기 이전
    @Before("authCut() || userCut() || adminCut() || postCut() || commentCut() || followCut() || likeCut()")
    public void loggingArgs(JoinPoint joinPoint) { // cut()의 대상 메소드
        // 입력값 가져오기
        Object[] args = joinPoint.getArgs();

        // 클래스명
        String className = joinPoint.getTarget()
                .getClass()
                .getSimpleName();

        // 메소드명
        String methodName = joinPoint.getSignature()
                .getName();

        // 입력값 로깅하기
        for (Object obj : args) { // foreach 문
            log.info("{}#{}의 입력값 => {}", className, methodName, obj);
        }
    }

    // 실행 시점 설정: cut()에 지정된 대상 호출 성공 후
    @AfterReturning(value = "authCut() || userCut() || adminCut() || postCut() || commentCut() || followCut() || likeCut()", returning = "returnObj")
    public void loggingReturnValue(JoinPoint joinPoint, // cut()의 대상 메소드
                                   Object returnObj) { // 리턴값

        // 클래스명
        String className = joinPoint.getTarget()
                .getClass()
                .getSimpleName();

        // 메소드명
        String methodName = joinPoint.getSignature()
                .getName();

        // 반환값 로깅
        log.info("{}#{}의 반환값 => {}", className, methodName, returnObj);
    }
}
