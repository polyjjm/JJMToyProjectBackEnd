package com.example.demo.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    protected Map<String,Object> handleCustomException(CustomException e) {
        System.out.println("??");
        ErrorCode errorMessage = ErrorCode.ACCESS_TOKEN_EXPIRED;

        Map<String,Object> resutMap = new HashMap<>();
        resutMap.put("errorMessage" , errorMessage.toString());
        resutMap.put("errorCode" , "402");
        return resutMap;
        //return null;
    }
}
