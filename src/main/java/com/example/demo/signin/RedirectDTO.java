package com.example.demo.signin;

import lombok.Data;

@Data
public class RedirectDTO {
    private String code;
    // Optional: which origin's callback URL the frontend actually used (must be one of the
    // URIs registered in the Kakao Developers console). Falls back to oauth.kakao.redirect-uri
    // when not sent, so this stays compatible with older frontend builds.
    private String redirectUri;
}
