package com.example.demo.config;


import com.example.demo.common.CustomException;
import com.example.demo.signin.GoogleOauth2LoginSuccess;
import com.example.demo.signin.JwtTokenFilter;
import com.example.demo.signin.LogoutService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class securityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    //private final GoogleOauth2LoginSuccess googleOauth2LoginSuccess;

    private final LogoutService logoutService;

    public securityConfig(JwtTokenFilter jwtTokenFilter ,LogoutService logoutService ) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.logoutService = logoutService;
        //this.googleOauth2LoginSuccess = googleOauth2LoginSuccess;
    }
    @Bean
    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(configurationSource()))
            .csrf(AbstractHttpConfigurer::disable) //csrf비활성화
//                Basic인증 비활성화
//                Basic인증은 사용자이름과 비밀번호를 Base64로 인코딩하여 인증값으로 활용
            .httpBasic(AbstractHttpConfigurer::disable)
//                세션방식을 비활성화
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                특정 url패턴에 대해서는 인증처리(Authentication객체생성) 제외
            .authorizeHttpRequests((auth) -> auth
                    // /board/** used to be blanket permitAll, which meant board write endpoints
                    // (subMit/update/delete) were reachable with no login at all - only the read
                    // endpoints below are meant to be public. Everything else under /board/**
                    // (subMit/update/delete, and comment insert/delete) now falls through to
                    // anyRequest().authenticated().
                    // /api/chat/roomInfo, /readStatus join /history in being permitAll - guest
                    // chat participants (NicknameInputPage.tsx) have no JWT at all, only the
                    // chosen nickname stored as their user_id, so these must stay reachable the
                    // same way /api/chat/history already was. All 3 do their own isMemberOfRoom
                    // check app-side (see chatController.java) instead of relying on Spring
                    // Security's identity, since "identity" here can be an unauthenticated guest
                    // nickname. /api/chat/rooms, /createRoom, /unreadCount, /uploadImage are
                    // deliberately NOT here - guests never reach the room-list panel/bell at all
                    // (see appShell.tsx), so those stay behind anyRequest().authenticated().
                    .requestMatchers( "/api/chat/joinGuestRoom", "/api/chat/roomInfo/**", "/api/chat/readStatus/**", "/menu/**" ,"/ws-chat/**", "/api/chat/history/**","/member/google/doLogin", "/member/**","/member/kakao/doLogin","/member/kakao/doLogin/**" ,"/oauth2/**","/auth/**",
                            "/board/select", "/board/boardSearch", "/board/categoryTree", "/board/view", "/board/comment/list").permitAll().anyRequest().authenticated())
//                UsernamePasswordAuthenticationFilter 이 클래스에서 폼로그인 인증을 처리
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                oauth로그인이 성공했을경우 실행할 클래스 정의
            //.oauth2Login(o -> o.successHandler(googleOauth2LoginSuccess))
            .build();
    }


    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(Arrays.asList("http://3.36.127.136:80"));
        configuration.setAllowedOrigins(Arrays.asList(
                "http://3.36.127.136:80" ,
                "http://3.36.127.136",
                "http://jjmdev.co.kr",
                "http://jjmdev.co.kr:8020",
                "http://localhost:3000",
                "http://3.36.127.136:8020" ,
                "http://3.36.127.136:3000",
                "http://jjmdev.co.kr:8020",
                "http://jjmdev.co.kr:8082",
                "http://jjmdev.co.kr:80",
                "http://jjmdev.co.kr:3000",
                "http://3.36.127.136:8082",
                "http://localhost:8082",
                "http://3.36.127.136:8000",
                "http://localhost:8000",
                "https://app.jjmlab.com",
                "https://api.jjmlab.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("*")); //모든 HTTP메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); //모든 헤더값 허용
        configuration.setAllowCredentials(true); //자격증명허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        모든 url패턴에 대해서 cors 허용 설정
        source.registerCorsConfiguration("/**", configuration);
        return  source;
    }
}
