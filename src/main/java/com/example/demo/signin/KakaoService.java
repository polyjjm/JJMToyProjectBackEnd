package com.example.demo.signin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;


    public AccessTokenDTO getAccessToken(String code){
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<AccessTokenDTO> response =  restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(params)
                .retrieve()
                .toEntity(AccessTokenDTO.class);

        System.out.println("응답 accesstoken JSON " + response.getBody());
        return response.getBody();
    }

    public KakaoProfileDTO getKakaoProfile(String token){
        RestClient restClient = RestClient.create();
        ResponseEntity<KakaoProfileDTO> response =  restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer "+token)
                .retrieve()
                .toEntity(KakaoProfileDTO.class);
        System.out.println("profile JSON" + response.getBody());
        return response.getBody();
    }
}
