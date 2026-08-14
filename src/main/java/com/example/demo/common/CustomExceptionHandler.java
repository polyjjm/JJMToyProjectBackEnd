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

        // Most existing error codes are auth/token related (401), so that's the default.
        // Added explicit cases as new non-401 codes were introduced rather than keeping
        // the old binary "MEMBER_PHONE_EXIST vs everything else" check.
        HttpStatus status = switch (errorMessage) {
            case MEMBER_PHONE_EXIST -> HttpStatus.CONFLICT;
            case COMMENT_FORBIDDEN -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.UNAUTHORIZED;
        };
        return new ResponseEntity<>(resutMap, status);
    }
}
