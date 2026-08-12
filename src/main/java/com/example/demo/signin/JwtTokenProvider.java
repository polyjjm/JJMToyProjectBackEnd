package com.example.demo.signin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String secretKey;
    private final int expirationMinutes;
    private final int refreshExpirationMinutes;
    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                             @Value("${jwt.expiration}") int expirationMinutes,
                             @Value("${jwt.refresh-expiration}") int refreshExpirationMinutes) {
        this.secretKey = secretKey;
        this.expirationMinutes = expirationMinutes;
        this.refreshExpirationMinutes = refreshExpirationMinutes;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }
    public String createToken(String email, String role){
//        claims는 jwt토큰의 payload부분을 의미
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ Duration.ofMinutes(expirationMinutes).toMillis()))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }

    public String refreshToken(String email, String role){
//        claims는 jwt토큰의 payload부분을 의미
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ Duration.ofMinutes(refreshExpirationMinutes).toMillis()))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }

    public boolean validateToken(String token) {
        Optional<Claims> optionalClaims = getClaimsFromToken(token);

        if (optionalClaims.isPresent()) {
            Date expiration = optionalClaims.get().getExpiration();
            return expiration.after(new Date());
        } else {
            // 토큰이 유효하지 않거나 파싱 실패
            return false;
        }
    }

    // JWT 토큰에서 Claims 추출
    private Optional<Claims> getClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("JWT validation failed: {}", e.toString());
            return Optional.empty();
        }
    }

    public Long getExpiration(String accessToken){
        //에세스 토큰 만료시간
        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody()
                .getExpiration();
        //현재시간
        long now = new Date().getTime();
        return (expiration.getTime()-now);
    }
}
