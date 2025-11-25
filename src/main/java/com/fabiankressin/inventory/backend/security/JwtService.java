package com.fabiankressin.inventory.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    // ============================================================
    // 1. Generate JWT with username + role
    // ============================================================
    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority(); // "ROLE_ADMIN" or "ROLE_USER"

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .sign(getAlgorithm());
    }

    // ============================================================
    // 2. Extract username from token
    // ============================================================
    public String extractUsername(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getSubject();
    }

    // ============================================================
    // 3. Extract role from token
    // ============================================================
    public String extractRole(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getClaim("role").asString();  // "ROLE_ADMIN"
    }

    // ============================================================
    // 4. Extract token from HttpOnly cookie
    // ============================================================
    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // ============================================================
    // 5. Helpers for /auth/me
    // ============================================================
    public String extractUsernameFromCookie(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null) return null;
        return extractUsername(token);
    }

    public String extractRoleFromCookie(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null) return null;
        return extractRole(token);
    }

    // ============================================================
    // 6. Validation
    // ============================================================
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}
