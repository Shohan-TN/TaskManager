package com.testproject.taskmanager.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    private SecretKey getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, Long userId) {
        return Jwts.builder()
                .subject(username)
                .claim("user_id", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
             return false;
        }
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaims(token).getSubject();
    }
    public Long extractUserIdFromAccessToken(String token) {
        return extractClaims(token).get("user_id", Long.class);
    }


    public String generateRefreshToken(String username, Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isRefreshTokenInValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Long extractUserIdFromRefreshToken(String token) {
        return Long.valueOf(extractClaims(token).getSubject());
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaims(token).get("username", String.class);
    }

    public long getRefreshTokenRemainingMillis(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
