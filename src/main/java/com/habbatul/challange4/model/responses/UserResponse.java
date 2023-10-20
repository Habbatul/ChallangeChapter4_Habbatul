package com.habbatul.challange4.model.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    private String username;
    private String emailAddress;
    private String password;
}
