package com.example.demo.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ACCESS_TOKEN_EXPIRED ("401","만료된 access token 입니다."),
    REFRESH_TOKEN_EXPIRED("402","만료된 refresh token 입니다."),
    REFRESH_TOKEN_NOT_MATCH("403","유효하지 않은 refresh token 입니다. 다시 로그인하세요."),
    MEMBER_PHONE_EXIST("404","이미 등록된 핸드폰 번호입니다.");

    private final String errorMessage;
    private final String errorCode;

}
