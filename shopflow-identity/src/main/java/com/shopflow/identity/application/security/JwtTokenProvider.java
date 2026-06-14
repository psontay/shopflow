package com.shopflow.identity.application.security;

import com.shopflow.identity.domain.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider implements TokenProviderPort {

    private final SecretKey key;
    private final long accessTokenExpirationMs;

    public JwtTokenProvider(@Value("${app.security.jwt.secret}") String secretKey,
                            @Value("${app.security.jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    @Override
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);
        return Jwts.builder()
                   .subject(user.getId()
                                .toString())
                   .claim("username", user.getUsername())
                   .claim("email", user.getEmail())
                   .issuedAt(now)
                   .expiration(expiryDate)
                   .signWith(key)
                   .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }

}
