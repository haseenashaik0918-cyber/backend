package com.taskmanager.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-that-is-at-least-32-bytes";

    @Test
    void createsAndParsesTokenForUser() {
        JwtService jwtService = new JwtService(SECRET, 86_400_000);

        String token = jwtService.createToken("user-123");

        assertEquals("user-123", jwtService.getUserId(token));
    }

    @Test
    void rejectsTokenWithInvalidSignature() {
        JwtService jwtService = new JwtService(SECRET, 86_400_000);
        String token = jwtService.createToken("user-123");

        assertThrows(JwtException.class, () -> jwtService.getUserId(token + "invalid"));
    }

    @Test
    void rejectsWeakSecret() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService("too-short", 86_400_000));

        assertEquals("JWT_SECRET must contain at least 32 bytes.", exception.getMessage());
    }
}
