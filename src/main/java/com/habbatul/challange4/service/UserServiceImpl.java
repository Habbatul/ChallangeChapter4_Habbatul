package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;
import com.habbatul.challange4.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserResponse toUserResponse(User user) {
        log.debug("Memberikan response user");

        return UserResponse.builder()
                .emailAddress(user.getEmailAddress())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    @Override
    public UserResponse updateUser(String username, UpdateUserRequest userReq) {
        log.debug("Menjalankan service updateUser");

        User user = User.builder()
                .username(username)
                .emailAddress(userReq.getEmailAddress())
                .password(userReq.getPassword())
                .build();

        Optional<User> userByName = userRepository.findUserByUsername(user.getUsername());
        if (userByName.isPresent()) {
            User oldUser = userByName.get();
            oldUser.setEmailAddress(user.getEmailAddress() != null ? user.getEmailAddress() : oldUser.getEmailAddress());
            oldUser.setUsername(user.getUsername() != null ? user.getUsername() : oldUser.getUsername());
            oldUser.setPassword(user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : oldUser.getPassword());
            userRepository.save(oldUser);
            log.info("User berhasil diupdate");
            return toUserResponse(oldUser);
        } else {
            log.error("User tidak ditemukan.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User tidak ditemukan");
        }
    }


    @Transactional
    @Override
    public void deleteUser(String username) {
        log.debug("Menjalankan service deleteUser");

        User user = new User();
        user.setUsername(username);

        Optional<User> userByName = userRepository.findUserByUsername(user.getUsername());
        if (userByName.isPresent()) {
            userRepository.delete(userByName.get());
            log.info("User berhasil dihapus");
        } else {
            log.error("Username tidak ditemukan");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User tidak ditemukan");
        }
    }
}
