package com.habbatul.challange4.service;

import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;

public interface UserService {
    UserResponse updateUser(String username, UpdateUserRequest userReq);
    void deleteUser(String username);

}
