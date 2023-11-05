package com.habbatul.challange4.other.security;

import com.habbatul.challange4.model.security.UserDetailsImpl;
import com.habbatul.challange4.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private HttpServletRequest request;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    @Test
    public void testGenerateJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "testuser@example.com", "password", null);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtil.generateJwtToken(authentication);

        String username = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertEquals("testuser", username);
    }

    @Test
    public void testGetUsernameFromJwtToken() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String username = jwtUtil.getUsernameFromJwtToken(token);

        assertEquals("testUser", username);
    }

    @Test
    public void testValidateJwtTokenValid() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtil.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    public void testValidateJwtTokenInvalid() {
        String token = "invalidToken";

        boolean isValid = jwtUtil.validateJwtToken(token);

        assertFalse(isValid);
    }

    @Test
    public void testExtractTokenFromCookies() {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("token", "testCookieToken");

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = jwtUtil.extractToken(request);

        assertEquals("testCookieToken", token);
    }

    @Test
    public void testExtractTokenFromHeader() {
        Cookie[] cookies = null;

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn("Bearer testHeaderToken");

        String token = jwtUtil.extractToken(request);

        assertEquals("testHeaderToken", token);
    }

    @Test
    public void testExtractTokenNull() {
        Cookie[] cookies = null;

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = jwtUtil.extractToken(request);

        assertNull(token);
    }
}
