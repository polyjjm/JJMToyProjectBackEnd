package com.example.demo.signin;

import com.example.demo.common.CustomException;
import com.example.demo.common.CustomExceptionHandler;
import com.example.demo.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenFilter extends GenericFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException  {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        String token = httpServletRequest.getHeader("Authorization");
        try {
            if(token !=null){
                if(!token.startsWith("Bearer ")){
                    throw new AuthenticationServiceException("Bearer 형식 아닙니다.");
                }
                String jwtToken = token.substring(7);
//            token 검증 및 claims(payload) 추출
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();
//            Authentication객체 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch(ExpiredJwtException e){
            // 이 필터는 permitAll인 경로(예: /board/comment/list 조회)에도 똑같이 실행된다.
            // 예전에는 토큰이 만료됐다는 이유만으로 무조건 401을 반환했는데, 그러면
            // "비로그인으로도 봐야 하는" 공개 API까지 "로그인은 했었지만 토큰이 오래된" 사용자한테
            // 막혀버리는 문제가 있었다. 여기서는 인증 컨텍스트를 비워서 "비로그인 상태"로 취급하고
            // 실제로 로그인이 필요한 경로였다면, 그건 뒤쪽의 authorizeHttpRequests가 알아서
            // 401/403으로 막아준다 - 필터가 미리 판단해서 막을 필요가 없다.
            e.printStackTrace();
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);

        } catch (Exception e ){
            // 위와 같은 이유로: 토큰 형식이 이상하거나 서명 검증에 실패해도, 여기서 바로 401을
            // 응답해버리지 않고 비로그인 상태로 넘긴다. permitAll 경로면 정상적으로 처리되고,
            // 인증이 필요한 경로면 뒤쪽 필터체인에서 401/403으로 걸러진다.
            e.printStackTrace();
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
        }
    }
}