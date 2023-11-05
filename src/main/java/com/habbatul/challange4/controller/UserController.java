package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.CreateUserRequest;
import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.UserService;
import com.habbatul.challange4.utils.AuthExtractor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthExtractor authExtractor;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Menambahkan user (sementara saya tidak hapus)")
    @PostMapping(value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> addUser(@RequestBody CreateUserRequest user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(WebResponse.<UserResponse>builder()
                .data(userService.addUser(user))
                .build());
    }

    @Operation(summary = "Mengupdate user (berdasarkan username JWTtoken bisa dari cookies atau header Authorization)")
    @PutMapping(value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> updateUser(HttpServletRequest request,
                                                                @RequestBody UpdateUserRequest user) {

        //olah request untuk mendapatkan username cookie atau header
        String username = authExtractor.ExtractorUsernameFromHeaderCookie(request);

        return ResponseEntity.ok(WebResponse.<UserResponse>builder()
                .data(userService.updateUser(username, user))
                .build());
    }

    @Operation(summary = "Menghapus user (berdasarkan username JWTtoken bisa dari cookies atau header Authorization)")
    @DeleteMapping(value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteUser(HttpServletRequest request) {
        //olah request untuk mendapatkan username cookie atau header
        //String username = authExtractor.ExtractorUsernameFromHeaderCookie(request);

        userService.deleteUser(authExtractor.ExtractorUsernameFromHeaderCookie(request));
        return ResponseEntity.ok(WebResponse.<String>builder().data("OK").build());
    }


}