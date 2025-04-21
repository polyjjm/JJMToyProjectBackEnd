package com.example.demo.signin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class tokenDTO {
    private String grantType;   // Bearer
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
