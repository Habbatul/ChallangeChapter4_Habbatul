package com.habbatul.challange4.utils;

import com.habbatul.challange4.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper untuk olah request untuk mendapatkan username cookie atau header bearer
 */

@Component
public class AuthExtractor {

    @Autowired
    private JwtUtil jwtTokenUtil;

    public String ExtractorUsernameFromHeaderCookie(HttpServletRequest request){

        String token = jwtTokenUtil.extractToken(request);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token tidak ada");
        }

        return jwtTokenUtil.getUsernameFromJwtToken(token);
    }
}
