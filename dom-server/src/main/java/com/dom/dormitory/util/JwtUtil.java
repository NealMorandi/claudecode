package com.dom.dormitory.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT ユーティリティクラス
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    /** 署名キーを生成 */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** JWTトークンを生成 */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expire * 1000);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** トークンからClaimsを取得 */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** トークンからユーザー名を取得 */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** トークンからユーザーIDを取得 */
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /** トークンからロールを取得 */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /** トークンの有効性を検証 */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT検証エラー: {}", e.getMessage());
            return false;
        }
    }

    /** トークンの有効期限を取得 */
    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }
}
