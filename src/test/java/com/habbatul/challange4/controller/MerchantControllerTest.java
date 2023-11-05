package com.habbatul.challange4.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habbatul.challange4.entity.security.Roles;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.enums.MerchantStatus;
import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.service.MerchantService;
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

import java.util.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class MerchantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    private MerchantService merchantService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testShowOpenMerchants() throws Exception {
        List<MerchantResponse> merchantResponses = new ArrayList<>();

        when(merchantService.showOpenMerchant()).thenReturn(merchantResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/merchant")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testCreateMerchant() throws Exception {
        // Mengambil token dengan metode authenticateAndGetToken
        String token = loginFirst();

        // Simulasikan data yang akan dikembalikan oleh merchantService
        MerchantResponse merchantResponse = MerchantResponse.builder()
                .merchantName("Merchant1")
                .merchantLocation("Example alamat")
                .open(MerchantStatus.OPEN)
                .build();

        when(merchantService.addMerchant(Mockito.any(CreateMerchantRequest.class))).thenReturn(merchantResponse);

        CreateMerchantRequest createMerchantRequest = CreateMerchantRequest.builder()
                .merchantName("Merchant1")
                .merchantLocation("Example alamat")
                .open(MerchantStatus.OPEN)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/merchant")
                        .header("Authorization", "Bearer " + token)  // Gunakan token di sini
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createMerchantRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }


    @Test
    public void testEditMerchant() throws Exception {
        // Membuat token dengan role MERCHANT
        String token = loginFirst();

        // Simulasikan data yang akan dikembalikan oleh merchantService
        MerchantResponse merchantResponse = new MerchantResponse("Merchant1", "Example alamat", MerchantStatus.OPEN);
        // Set data merchant yang diharapkan

        when(merchantService.editStatus(Mockito.anyString(), Mockito.any(UpdateMerchantRequest.class)))
                .thenReturn(merchantResponse);

        UpdateMerchantRequest updateMerchantRequest = new UpdateMerchantRequest();
        // Set data updateMerchantRequest sesuai kebutuhan

        mockMvc.perform(MockMvcRequestBuilders.put("/merchant/merchantName")
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateMerchantRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    private String loginFirst() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));

        Set<Roles> mySet = new HashSet<>();
        mySet.add(Roles.builder().roleName(ERole.MERCHANT).build());
        user.setRoles(mySet);

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
