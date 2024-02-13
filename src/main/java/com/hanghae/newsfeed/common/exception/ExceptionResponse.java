package com.hanghae.newsfeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private Boolean ok;
    private int statusCode;
    private String message;

    public ExceptionResponse(String message, int statusCode) {
        this.ok = false;
        this.message = message;
        this.statusCode = statusCode;
    }
}