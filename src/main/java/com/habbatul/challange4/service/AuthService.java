package com.habbatul.challange4.service;

import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.requests.authreq.SignupRequest;
import com.habbatul.challange4.model.responses.JwtResponse;
import com.habbatul.challange4.model.responses.WebResponse;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest login, HttpServletResponse response);
    WebResponse<String> registerUser(SignupRequest signupRequest);
}
