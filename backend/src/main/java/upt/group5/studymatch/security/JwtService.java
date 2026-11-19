package upt.group5.studymatch.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import upt.group5.studymatch.model.User;

@Service
public class JwtService {

    private final String jwtSecret;

    // get the jwt secret from the application configuration
    public JwtService(@Value("${app.jwt.secret:}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    // get the signing key from the configured secret
    private SecretKey getSigningKey() {

        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured");
        }

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 32 bytes"
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generate a token with the user id and role
    public String generateToken(User user) {

        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(8 * 60 * 60);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // validate the token signature and expiration
    public Claims validateToken(String token) throws JwtException {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
