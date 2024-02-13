package com.hanghae.newsfeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class HttpException extends RuntimeException {
    private boolean ok;
    private String message;
    private HttpStatus httpStatus;
}