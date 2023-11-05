package com.habbatul.challange4.other.security;

import com.habbatul.challange4.security.AuthEntryPointJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
    }

    @Test
    void testCommence() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = mock(AuthenticationException.class);

        authEntryPointJwt.commence(request, response, authException);

        assertEquals( "application/json",response.getContentType());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }
}

