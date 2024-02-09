package com.hanghae.newsfeed.security.jwt;

import com.hanghae.newsfeed.user.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider { // 토큰을 만들고 분석하는 클래스
    @Value("${jwt.secret-key.access}")
    private String accessSecretKey;
    @Value("${jwt.secret-key.refresh}")
    private String refreshSecretKey;
    @Value("${expired-time.access}")
    private Long accessTokenExpiredTime;
    @Value("${expired-time.refresh}")
    private Long refreshTokenExpiredTime;

    // 토큰 생성
    public String generate(String email, UserRoleEnum role, JwtTokenType type){
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("userRole", role); // 사용자 권한을 클레임에 추가

        Date date = new Date();

        String secretKey;
        Long expiredTime;
        if (type.equals(JwtTokenType.ACCESS)) {
            secretKey = accessSecretKey;
            expiredTime = accessTokenExpiredTime;
        } else {
            secretKey = refreshSecretKey;
            expiredTime = refreshTokenExpiredTime;
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + expiredTime))
                .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 만료 확인
    public boolean isExpired(String token, JwtTokenType type) {
        Date expiredDate = extractClaims(token, type).getExpiration();
        return expiredDate.before(new Date());
    }

    // 만료 시간 확인
    public long getExpiredTime(String token, JwtTokenType type) {
        Date expiredDate = extractClaims(token, type).getExpiration();
        Date currentDate = new Date();
        return expiredDate.getTime() - currentDate.getTime();
    }

    // 토큰에서 사용자 정보 가져오기
    public String getEmail(String token, JwtTokenType type) {
        return extractClaims(token, type).get("email", String.class);
    }

    // claims 추출하는 메소드
    private Claims extractClaims(String token, JwtTokenType type) {
        if (type.equals(JwtTokenType.ACCESS)) {
            return Jwts.parserBuilder().setSigningKey(getKey(accessSecretKey))
                    .build().parseClaimsJws(token).getBody();
        }
        return Jwts.parserBuilder().setSigningKey(getKey(refreshSecretKey))
                .build().parseClaimsJws(token).getBody();
    }

    private Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}