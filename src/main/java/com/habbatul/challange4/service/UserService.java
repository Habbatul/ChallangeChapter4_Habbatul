package com.habbatul.challange4.service;

import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;

public interface UserService {
    //addUser ada di authservice dengan nama method registerUser
//    UserResponse addUser(CreateUserRequest user);
    UserResponse updateUser(String username, UpdateUserRequest userReq);
    void deleteUser(String username);

}
