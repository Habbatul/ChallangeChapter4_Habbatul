package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.model.UserResponse;

public interface UserService {
    UserResponse addUser(User user);
    UserResponse updateUser(User user);
    void deleteUser(User user);

}
