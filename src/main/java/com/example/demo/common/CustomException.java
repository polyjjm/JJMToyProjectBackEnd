package com.example.demo.common;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final  String errorCode;
    private final  ErrorCode errorMessage;
    public CustomException(ErrorCode errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }




}
