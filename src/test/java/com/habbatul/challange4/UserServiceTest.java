package com.habbatul.challange4;

import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.model.requests.CreateUserRequest;
import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    void testAddUser() {
        CreateUserRequest user = CreateUserRequest.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();

        //service untuk menambahkan
        UserResponse response = userService.addUser(user);

        //cek apakah respond sudah sesuai dengan user yang dimasukkan
        assertEquals("test@contoh.com", response.getEmailAddress());
        assertEquals("testusername", response.getUsername());
        assertEquals("testpassword", response.getPassword());

        //Cek apakah pada db hasilnya sama
        User savedUser = userRepository.findUserByUsername(user.getUsername()).orElse(null);
        assertEquals("test@contoh.com", savedUser.getEmailAddress());
        assertEquals("testusername", savedUser.getUsername());
        assertEquals("testpassword", savedUser.getPassword());
    }

    @Test
    void testAddSameName() {
        User user = User.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();
        userRepository.save(user);

        CreateUserRequest user2 = CreateUserRequest.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();
        //service untuk menambahkan

        assertThrows(ResponseStatusException.class, ()->userService.addUser(user2));
    }


    @Test
    void testUpdateUser() {
        User user = User.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();
        userRepository.save(user);

        //pengubahan setelah user disimpan ke db
        user.setEmailAddress("update@contoh.com");

        UpdateUserRequest user2 = UpdateUserRequest.builder()
                .emailAddress(user.getEmailAddress())
                .password(user.getPassword())
                .build();

        //service untuk update
        UserResponse response = userService.updateUser(user.getUsername(), user2);

        //cek apakah email berhasil diganti menjadi email yang diupdate
        assertEquals("update@contoh.com", response.getEmailAddress());

        //cek pada database perubahan yang terjadi
        User userBerubah = userRepository.findById(user.getUserId()).orElse(null);
        assertEquals("update@contoh.com", userBerubah.getEmailAddress());
    }


    @Test
    void testUpdateUserNotFound() {
        UpdateUserRequest user = UpdateUserRequest.builder()
                .build();
        String username ="ERROR";

        //cek apakah exception berjalan
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(username, user));
    }


    @Test
    void testDeleteUser() {
        User user = User.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();
        userRepository.save(user);


        //menghapus user yang sudah disimpan
        userService.deleteUser(user.getUsername());

        //cek bahwa pada db data telah terhapus
        User deletedUser = userRepository.findById(user.getUserId()).orElse(null);
        assertNull(deletedUser);
    }


    @Test
    void testDeleteUserNotFound() {
        CreateUserRequest user = CreateUserRequest.builder()
                .username("0000")//masukin id yang ga ada
                .build();

        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(user.getUsername()));
    }

}