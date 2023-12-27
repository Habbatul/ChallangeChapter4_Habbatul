package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.requests.authreq.SignupRequest;
import com.habbatul.challange4.model.responses.JwtResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Melakukan authentikasi user untuk mendapat hak akses (login)")
    @PermitAll
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest login, HttpServletResponse response) {
        return ResponseEntity.ok().body(authService.authenticateUser(login, response));
    }

    @Operation(summary = "Menambahkan user (register)")
    @PostMapping("/signup")
    public ResponseEntity<WebResponse<String>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok().body(authService.registerUser(signupRequest));
    }
}