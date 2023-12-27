package com.habbatul.challange4.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habbatul.challange4.entity.security.Roles;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.model.requests.CreateProductRequest;
import com.habbatul.challange4.model.requests.UpdateProductRequest;
import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.responses.ProductPaginationResponse;
import com.habbatul.challange4.model.responses.ProductResponse;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.service.ProductService;
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
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testShowProduct() throws Exception {
        String token = loginFirst();

        //coba simulasi data response paginationnya
        ProductPaginationResponse productResponse = new ProductPaginationResponse(
                //coba pakek isian data langsung, dengan beberapa product
                Arrays.asList(
                        new ProductResponse("ProductCode1", "Product1", 10.0, "Merchant1"),
                        new ProductResponse("ProductCode2", "Product2", 15.0, "Merchant2")
                ),
                1, 1
        );

        //buat skenario service
        when(productService.showProduct(Mockito.anyInt())).thenReturn(productResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/product")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testAddProduct() throws Exception {
        String token = loginFirst();

        //coba simulasi data data yang akan dikembalikan oleh productService
        ProductResponse productResponse = ProductResponse.builder()
                .productName("Product1")
                .price(100.0)
                .merchantName("Example Merchant")
                .build();

        when(productService.addProduct(Mockito.any(CreateProductRequest.class))).thenReturn(productResponse);

        CreateProductRequest createProductRequest = CreateProductRequest.builder()
                .productName("Product1")
                .price(100.0)
                .merchantName("Example Merchant")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .header("Authorization", "Bearer " + token)  // Gunakan token di sini
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createProductRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        String token = loginFirst();

        //coba simulasi data data yang akan dikembalikan oleh productService
        ProductResponse productResponse = ProductResponse.builder()
                .productCode("ProductCode1")
                .productName("UpdatedProduct")
                .price(150.0)
                .merchantName("UpdatedMerchant")
                .build();

        when(productService.updateProduct(Mockito.any(UpdateProductRequest.class), Mockito.anyString()))
                .thenReturn(productResponse);

        UpdateProductRequest updateProductRequest = UpdateProductRequest.builder()
                .productName("UpdatedProduct")
                .price(150.0)
                .merchantName("UpdatedMerchant")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/product/ProductCode1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateProductRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        String token = loginFirst();

        mockMvc.perform(MockMvcRequestBuilders.delete("/product/ProductCode1")
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
        roles.add(Roles.builder().roleName(ERole.MERCHANT).build());
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
