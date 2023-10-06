package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.UserResponse;
import com.habbatul.challange4.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;


    @Override
    public UserResponse addUser(User user) {
        log.debug("Menjalankan service addUser");
        if (!userRepository.existsByUsername(user.getUsername())) {
            userRepository.save(user);
            log.info("user berhasil ditambahkan");
            return toUserResponse(user);
        } else {
            log.error("User telah ada");
            throw new CustomException("User is exist");
        }
    }

    private UserResponse toUserResponse(User user) {
        log.debug("Memberikan response user");
        return UserResponse.builder()
                .emailAddress(user.getEmailAddress())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }


    @Override
    public UserResponse updateUser(User user) {
        log.debug("Menjalankan service updateUser");
        Optional<User> userByID = userRepository.findById(user.getUserId());
        if (userByID.isPresent()) {
            User oldUser = userByID.get();
            oldUser.setEmailAddress(user.getEmailAddress() != null ? user.getEmailAddress() : oldUser.getEmailAddress());
            oldUser.setUsername(user.getUsername() != null ? user.getUsername() : oldUser.getUsername());
            oldUser.setPassword(user.getPassword() != null ? user.getPassword() : oldUser.getPassword());
            userRepository.save(oldUser);
            log.info("User berhasil diupdate");
            return toUserResponse(oldUser);
        } else {
            log.error("User tidak ditemukan");
            throw new CustomException("User Not found");
        }
    }


    @Override
    public void deleteUser(User user) {
        log.debug("Menjalankan service deleteUser");
        if (userRepository.existsById(user.getUserId())) {
            userRepository.delete(user);
            log.info("User berhasil dihapus");
        } else {
            log.error("Username sudah digunakan");
            throw new CustomException("Username is Exist");
        }
    }
}
