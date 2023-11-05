package com.habbatul.challange4.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habbatul.challange4.entity.security.Roles;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.enums.OrderStatus;
import com.habbatul.challange4.model.requests.OrderDetailRequest;
import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.responses.OrderDetailResponse;
import com.habbatul.challange4.model.responses.OrderResponse;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.service.OrderService;
import com.habbatul.challange4.utils.AuthExtractor;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    private OrderService orderService;

    @MockBean
    private AuthExtractor authExtractor;

    @MockBean
    UserRepository userRepository;

    @Test
    public void testCreateOrder() throws Exception {
        String token = loginFirst();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderTime(LocalDateTime.now())
                .destinationAddress("Example alamat")
                .completed(OrderStatus.COMPLETE)
                .detailOrder(Collections.singletonList(
                        OrderDetailResponse.builder()
                                .quantity(1)
                                .totalPrice(10.0)
                                .productName("Product1")
                                .build()
                ))
                .pembeliName("newuser")
                .build();

        when(authExtractor.ExtractorUsernameFromHeaderCookie(Mockito.any())).thenReturn("newuser");
        when(orderService.createOrder(Mockito.anyString(), Mockito.any(OrderRequest.class))).thenReturn(orderResponse);

        OrderRequest orderRequest = OrderRequest.builder()
                .destinationAddress("Example alamat")
                .completed(OrderStatus.COMPLETE)
                .detailOrder(Collections.singletonList(
                        OrderDetailRequest.builder()
                                .quantity(1)
                                .productName("Product1")
                                .build()
                ))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/order")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testGetAllOrders() throws Exception {
        String token = loginFirst();

        List<OrderResponse> orderResponses = new ArrayList<>();

        when(authExtractor.ExtractorUsernameFromHeaderCookie(Mockito.any())).thenReturn("newuser");
        when(orderService.getOrderByUser(Mockito.anyString())).thenReturn(orderResponses);

        when(orderService.getOrderAll()).thenReturn(orderResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/admin")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testGetUserOrders() throws Exception {
        // Buat token untuk otorisasi
        String token = loginFirst();

        // Simulasikan data yang akan dikembalikan oleh orderService
        List<OrderResponse> orderResponses = new ArrayList<>();

        when(authExtractor.ExtractorUsernameFromHeaderCookie(Mockito.any())).thenReturn("newuser");
        when(orderService.getOrderByUser(Mockito.anyString())).thenReturn(orderResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/order")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    public void testMakeOrder() throws Exception {
        // Buat token untuk otorisasi
        String token = loginFirst();

        //pakai byte kosong karena emang ga ada filenya
        byte[] pdfBytes = new byte[0];

        when(authExtractor.ExtractorUsernameFromHeaderCookie(Mockito.any())).thenReturn("newuser");
        when(orderService.printOrder(Mockito.anyString())).thenReturn(pdfBytes);

        mockMvc.perform(MockMvcRequestBuilders.post("/order/print")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Order.pdf"))
                .andExpect(MockMvcResultMatchers.content().bytes(pdfBytes));
    }

    private String loginFirst() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));

        Set<Roles> mySet = new HashSet<>();
        mySet.add(Roles.builder().roleName(ERole.CUSTOMER).build());
        user.setRoles(mySet);

        //buat repository mock nya
        when(userRepository.save(user)).thenReturn(user);
        //simpan user sehingga mendapat role
        userRepository.save(user);

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
