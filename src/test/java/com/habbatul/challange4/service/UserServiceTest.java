package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.model.requests.CreateUserRequest;
import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.responses.UserResponse;
import com.habbatul.challange4.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceTest {
    @InjectMocks
    @Spy
    private UserServiceImpl userService;

    //harus ada agar tidak error
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        Mockito.reset(userRepository);
    }

    @Test
    void testAddUser() {
        CreateUserRequest user = CreateUserRequest.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();

        User savedUser = User.builder()
                .emailAddress(user.getEmailAddress())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.addUser(user);

        assertEquals(user.getEmailAddress(), response.getEmailAddress());
        assertEquals(user.getUsername(), response.getUsername());

        verify(userService).addUser(user);
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testAddSameName() {
        User existingUser = User.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();

        //skenario nya kalo username nya sama kayak diatas, maka return true
        Mockito.when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        CreateUserRequest user2 = CreateUserRequest.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();

        assertThrows(ResponseStatusException.class, () -> userService.addUser(user2));
        verify(userService,times(1)).addUser(user2);
        verify(userRepository, times(1))
                .existsByUsername(existingUser.getUsername());
    }

    @Test
    void testUpdateUser() {
        User existingUser = User.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();

        Mockito.when(userRepository.findUserByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UpdateUserRequest user2 = UpdateUserRequest.builder()
                .emailAddress("update@contoh.com")
                .password(existingUser.getPassword())
                .build();

        UserResponse response = userService.updateUser(existingUser.getUsername(), user2);

        assertEquals("update@contoh.com", response.getEmailAddress());

        verify(userService, times(1))
                .updateUser(existingUser.getUsername(), user2);
        verify(userRepository, times(1))
                .findUserByUsername(existingUser.getUsername());
        verify(userRepository, times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        String username = "ERROR";
        Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        UpdateUserRequest user = UpdateUserRequest.builder().build();

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(username, user));
        verify(userService).updateUser(username, user);
    }

    @Test
    void testDeleteUser() {
        User existingUser = User.builder()
                .emailAddress("test@contoh.com")
                .username("testusername")
                .password("testpassword")
                .build();

        Mockito.when(userRepository.findUserByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));


        assertDoesNotThrow(()->userService.deleteUser(existingUser.getUsername()));

        verify(userRepository, Mockito.times(1)).delete(existingUser);
        verify(userService, times(1)).deleteUser(existingUser.getUsername());
    }

    @Test
    void testDeleteUserNotFound() {
        String username = "0000";
        Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(username));
        verify(userService,times(1)).deleteUser(username);
    }
}
