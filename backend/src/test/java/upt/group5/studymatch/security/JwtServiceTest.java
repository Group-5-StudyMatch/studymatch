package upt.group5.studymatch.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import upt.group5.studymatch.model.Role;
import upt.group5.studymatch.model.User;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "StudyMatchTestSecretKeyWithAtLeast32Characters"
    );

    @Test
    void shouldGenerateValidTokenWithUserIdAndRole() {

        // create a test user
        User user = new User(
                "admin@test.com",
                "hashedPassword",
                Role.ADMIN
        );

        user.setId(1L);

        // generate a jwt
        String token = jwtService.generateToken(user);

        // validate the generated token
        Claims claims = jwtService.validateToken(token);

        // verify the token claims
        assertNotNull(token);
        assertEquals("1", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
    }

    @Test
    void shouldRejectExpiredToken() {

        // use the same secret configured in the jwt service
        String secret = "StudyMatchTestSecretKeyWithAtLeast32Characters";

        // create a token that expired one hour ago
        Instant now = Instant.now();

        String expiredToken = Jwts.builder()
                .subject("1")
                .claim("role", "ADMIN")
                .issuedAt(Date.from(now.minusSeconds(7200)))
                .expiration(Date.from(now.minusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                ))
                .compact();

        // verify that an expired token is rejected
        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.validateToken(expiredToken)
        );
    }

        @Test
        void shouldRejectTamperedToken() {

        // create a test user
        User user = new User(
                "admin@test.com",
                "hashedPassword",
                Role.ADMIN
        );

        user.setId(1L);

        // generate a valid jwt
        String token = jwtService.generateToken(user);

        // change the signature without changing the token claims
        String[] parts = token.split("\\.");
        String signature = parts[2];

        char replacement = signature.charAt(0) == 'A' ? 'B' : 'A';
        String tamperedSignature = replacement + signature.substring(1);

        String tamperedToken = parts[0] + "." + parts[1] + "." + tamperedSignature;

        // verify that the signature was modified
        assertNotEquals(token, tamperedToken);

        // verify that the modified token is rejected
        assertThrows(
                JwtException.class,
                () -> jwtService.validateToken(tamperedToken)
        );
        }


}
