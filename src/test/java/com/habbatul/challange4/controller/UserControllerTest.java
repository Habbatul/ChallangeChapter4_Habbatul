package com.habbatul.challange4.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habbatul.challange4.entity.security.Roles;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.model.requests.CreateUserRequest;
import com.habbatul.challange4.model.requests.UpdateUserRequest;
import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.responses.UserResponse;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.service.UserService;
import com.habbatul.challange4.utils.AuthExtractor;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthExtractor authExtractor;

    @MockBean
    private UserRepository userRepository;


    //cek hak akses nya sepertinya ada salah masalahnya tanpa authentikasi bisa jalan
    @Test
    public void testAddUser() throws Exception {
        String token = loginFirst();

        UserResponse userResponse = UserResponse.builder()
                .username("newuser")
                .emailAddress("newuser@example.com")
                .build();

        when(userService.addUser(Mockito.any(CreateUserRequest.class))).thenReturn(userResponse);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("newuser")
                .emailAddress("newuser@example.com")
                .password("password")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createUserRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        String token = loginFirst();

        UserResponse userResponse = UserResponse.builder()
                .username("newuser")
                .emailAddress("newuser@example.com")
                .build();

        when(authExtractor.extractorUsernameFromHeaderCookie(Mockito.any())).thenReturn("newuser");
        when(userService.updateUser(Mockito.anyString(), Mockito.any(UpdateUserRequest.class))).thenReturn(userResponse);

        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .emailAddress("newuser@example.com")
                .password("newpassword")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUserRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        String token = loginFirst();

        when(authExtractor.extractorUsernameFromHeaderCookie(Mockito.any())).thenReturn("newuser");
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    private String loginFirst() throws Exception {

        //coba simulasi data user yang akan digunakan untuk autentikasi
        User user = new User();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));

        Set<Roles> roles = new HashSet<>();
        roles.add(Roles.builder().roleName(ERole.CUSTOMER).build());
        user.setRoles(roles);

        when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

        LoginRequest loginRequest = new LoginRequest("username", "password");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonResponse = new ObjectMapper().readTree(response);
        return jsonResponse.get("token").asText();
    }
}

