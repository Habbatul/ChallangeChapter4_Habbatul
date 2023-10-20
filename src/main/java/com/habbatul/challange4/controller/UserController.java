package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.CreateUserRequest;
import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Menambahkan user")
    @PostMapping(value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> addUser(@RequestBody CreateUserRequest user) {

        UserResponse response = userService.addUser(user);
        WebResponse<UserResponse> userResponseWebResponse = WebResponse.<UserResponse>builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseWebResponse);
    }

    @Operation(summary = "Mengupdate user")
    @PutMapping(value = "/user/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> updateUser(@PathVariable String username,
                                                                @RequestBody UpdateUserRequest user) {

        UserResponse response = userService.updateUser(username, user);
        WebResponse<UserResponse> userResponseWebResponse = WebResponse.<UserResponse>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(userResponseWebResponse);
    }

    @Operation(summary = "Menghapus user")
    @DeleteMapping(value = "/user/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteUser(@PathVariable String username) {

        userService.deleteUser(username);
        return ResponseEntity.ok(WebResponse.<String>builder().data("OK").build());
    }


}