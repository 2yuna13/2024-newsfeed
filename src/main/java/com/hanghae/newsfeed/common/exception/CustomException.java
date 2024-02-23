package com.hanghae.newsfeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException { // 예외 상황 정의
    private CustomErrorCode errorCode;

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}