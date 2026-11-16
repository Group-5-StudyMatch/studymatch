package upt.group5.studymatch.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String jwtSecret;

    //spring looks for jwt.secret propertie. if its not config, use an empty string. 
    // in that case, token validation fail with an error message
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

    // validate the token signature and expiration
    public Claims validateToken(String token) throws JwtException {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
