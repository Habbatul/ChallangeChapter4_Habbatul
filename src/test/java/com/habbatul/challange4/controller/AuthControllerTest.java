package com.habbatul.challange4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.requests.authreq.SignupRequest;
import com.habbatul.challange4.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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

import javax.validation.constraints.Null;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    
    @Test
    public void testAuthenticateUser() throws Exception {
        User user = new User();
        user.setUsername("username");

        user.setPassword(passwordEncoder.encode("password"));

        //Simulasi hasil yang diharapkan dari userRepository
        when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

        LoginRequest loginRequest = new LoginRequest("username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is(Matchers.notNullValue())));
    }

    @Test
    public void testAuthenticateUserNotFound() throws Exception {
        User user = new User();
        user.setUsername("username");

        user.setPassword(passwordEncoder.encode("password"));

        //Simulasi hasil yang diharapkan dari userRepository
        when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest("username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testRegisterUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest("newuser", "newuser@e.com", null, "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("User registered successfully"));
    }

    @Test
    public void testRoleNotFound() throws Exception {
        Set<String> mySet = new HashSet<>();
        mySet.add("makanan");
        SignupRequest signupRequest = new SignupRequest("newuser", "newuser@e.com", mySet, "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testRoleFound() throws Exception {
        Set<String> mySet = new HashSet<>();
        mySet.add(ERole.CUSTOMER.name());
        SignupRequest signupRequest = new SignupRequest("newuser", "newuser@e.com", mySet, "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void testUsernameExist() throws Exception {
        Set<String> mySet = new HashSet<>();
        mySet.add(ERole.CUSTOMER.name());
        SignupRequest signupRequest = new SignupRequest("newuser", "newuser@e.com", mySet, "password");

        //buat seakan repository mengembalikan, user bila telah ada
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) //periksa status BAD_REQUEST
                .andReturn();

        // Periksa pesan kesalahan dalam respons
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Username telah ada")); //pesan kesalahan yang diharapkan
    }


    @Test
    public void testEmailExist() throws Exception {
        SignupRequest signupRequest2 = new SignupRequest("newuser", "newuser@e.com", null, "password");

        //buat seakan repository mengembalikan, email bila telah ada
        when(userRepository.existsByEmailAddress("newuser@e.com")).thenReturn(true);

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) //periksa status BAD_REQUEST
                .andReturn();

        //Periksa pesan kesalahan dalam respons
        String responseContent2 = result2.getResponse().getContentAsString();
        assertTrue(responseContent2.contains("Email telah ada"));//pesan kesalahan yang diharapkan
    }


}



