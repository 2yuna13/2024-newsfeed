package com.hanghae.newsfeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse {
    private CustomErrorCode errorCode;
    private String message;
}