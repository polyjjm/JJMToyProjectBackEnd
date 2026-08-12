package com.example.demo.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<Map<String,Object>> handleCustomException(CustomException e) {
        ErrorCode errorMessage = e.getErrorMessage();

        Map<String,Object> resutMap = new HashMap<>();
        resutMap.put("errorMessage" , errorMessage.getErrorMessage());
        resutMap.put("errorCode" , errorMessage.getErrorCode());

        HttpStatus status = errorMessage == ErrorCode.MEMBER_PHONE_EXIST
                ? HttpStatus.CONFLICT
                : HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(resutMap, status);
    }
}
