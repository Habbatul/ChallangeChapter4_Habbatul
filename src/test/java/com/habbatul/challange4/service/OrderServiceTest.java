package com.habbatul.challange4.service;

import net.sf.jasperreports.engine.*;
import com.habbatul.challange4.entity.*;
import com.habbatul.challange4.enums.OrderStatus;
import com.habbatul.challange4.model.requests.OrderDetailRequest;
import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderDetailResponse;
import com.habbatul.challange4.model.responses.OrderResponse;
import com.habbatul.challange4.repository.OrderDetailRepository;
import com.habbatul.challange4.repository.OrderRepository;
import com.habbatul.challange4.repository.ProductRepository;
import com.habbatul.challange4.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {
    @Spy
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        Mockito.reset(userRepository);
        Mockito.reset(productRepository);
        Mockito.reset(orderDetailRepository);
        Mockito.reset(orderRepository);
    }

    @Test
    void testCreateOrder() {
        User user = User.builder()
                .username("TestUser")
                .build();
        when(userRepository.findUserByUsername("TestUser")).thenReturn(Optional.of(user));

        Product product = Product.builder()
                .productName("TestProduct")
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .price(100.0)
                .build();
        when(productRepository.findByProductName("TestProduct")).thenReturn(Optional.of(product));

        Product product2 = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct2")
                .price(200.0)
                .build();
        when(productRepository.findByProductName("TestProduct2")).thenReturn(Optional.of(product2));

        OrderRequest order = OrderRequest.builder()
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();

        OrderDetailRequest orderDetail = OrderDetailRequest.builder()
                .productName("TestProduct")
                .quantity(2)
                .build();

        //buat 2 order yang memiliki product yang sama untuk test merge func
        OrderDetailRequest orderDetail2 = OrderDetailRequest.builder()
                .productName("TestProduct2")
                .quantity(2)
                .build();

        OrderDetailRequest orderDetail3 = OrderDetailRequest.builder()
                .productName("TestProduct2")
                .quantity(1)
                .build();

        order.setDetailOrder(Arrays.asList(orderDetail, orderDetail2, orderDetail3));

        //merespons dengan mengembalikan argumen yang diberikan pada panggilan metode tersebut dengan invocationOnMock (:-)
        //karena hanya satu argumen ke method save dan saveall maka beri indeks 0     (:-)
        when(orderRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(orderDetailRepository.saveAll(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        OrderResponse response = orderService.createOrder(user.getUsername(), order);

        //times nya 3 , karena orderdetail ada 3 kali instance (berisi product)
        verify(productRepository, times(3)).findByProductName(any());

        assertNotNull(response);
        assertEquals("TestAddresses", response.getDestinationAddress());
        assertEquals(order.getCompleted().toString(), response.getCompleted().toString());
        assertEquals("TestUser", response.getPembeliName());

        assertEquals(2, response.getDetailOrder().size());

        List<OrderDetailResponse> orderDetails = response.getDetailOrder();

        orderDetails.forEach(detail -> {
            if (detail.getProductName().equals("TestProduct2")) {
                assertEquals(3, detail.getQuantity());
                assertEquals(3 * product2.getPrice(), detail.getTotalPrice());
            } else if (detail.getProductName().equals("TestProduct")) {
                assertEquals(2, detail.getQuantity());
                assertEquals(2 * product.getPrice(), detail.getTotalPrice());
            }else{
                throw new RuntimeException("Ada list yang tidak sesuai");
            }
        });

        verify(userRepository, times(1)).findUserByUsername("TestUser");
        verify(orderService, times(1)).createOrder(user.getUsername(), order);
    }

    @Test
    void testCreateOrderWithNonExistingUser() {
        when(userRepository.findUserByUsername("NonExistingUser")).thenReturn(Optional.empty());

        OrderRequest order = OrderRequest.builder()
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();

        OrderDetailRequest orderDetail = OrderDetailRequest.builder()
                .productName("TestProduct")
                .quantity(2)
                .build();

        order.setDetailOrder(Arrays.asList(orderDetail));

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder("NonExistingUser", order));
        verify(userRepository, times(1)).findUserByUsername("NonExistingUser");
    }

    @Test
    void testCreateOrderWithNonExistingProduct() {
        User user = User.builder()
                .username("TestUser")
                .build();
        when(userRepository.findUserByUsername("TestUser")).thenReturn(Optional.of(user));

        when(productRepository.findByProductName("NonExistingProduct")).thenReturn(Optional.empty());

        OrderRequest order = OrderRequest.builder()
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();

        OrderDetailRequest orderDetail = OrderDetailRequest.builder()
                .productName("NonExistingProduct")
                .quantity(2)
                .build();

        order.setDetailOrder(Arrays.asList(orderDetail));

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder("TestUser", order));

        verify(userRepository, times(1)).findUserByUsername("TestUser");
        verify(productRepository, times(1)).findByProductName("NonExistingProduct");
    }

    @Test
    void testGetOrderByUser() {
        String username = "TestUser";

        Product product = Product.builder()
                .productName("TestProduct")
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .price(2500.0)
                .build();

        Order order1 = Order.builder()
                .orderId(1L)
                .orderTime(LocalDateTime.now())
                .orderDetails(Arrays.asList(OrderDetail.builder().orderDetailId(1L)
                        .product(product)
                        .quantity(2)
                        .totalPrice(5000.0)
                        .build()))
                .destinationAddress("TestAddresses1")
                .completed(OrderStatus.COMPLETE)
                .user(User.builder().username(username).build())
                .build();

        //seakan-akan result hasil join ada 2 order sehingga nanti harusnya hasilnya tetap 1
        when(orderRepository.findOrdersByUserUsername(username)).thenReturn(Arrays.asList(order1,order1));

        List<OrderResponse> response = orderService.getOrderByUser(username);

        assertNotNull(response);
        assertEquals(1, response.size());

        assertEquals("TestAddresses1", response.get(0).getDestinationAddress());
        assertEquals(OrderStatus.COMPLETE, response.get(0).getCompleted());


        verify(orderRepository, times(1)).findOrdersByUserUsername(username);
    }

    @Test
    void testGetOrderByUserWithEmptyResult() {
        String username = "TestUser";

        when(orderRepository.findOrdersByUserUsername(username)).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> orderService.getOrderByUser(username));

        verify(orderRepository, times(1)).findOrdersByUserUsername(username);
    }

    @Test
    void testGetOrderAll() {
        Product product = Product.builder()
                .productName("TestProduct")
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .price(2500.0)
                .build();

        Order order1 = Order.builder()
                .orderId(1L)
                .orderTime(LocalDateTime.now())
                .orderDetails(Arrays.asList(OrderDetail.builder().orderDetailId(1L)
                        .product(product)
                        .quantity(2)
                        .totalPrice(5000.0)
                        .build()))
                .destinationAddress("TestAddresses1")
                .completed(OrderStatus.COMPLETE)
                .user(User.builder().username("User1").build())
                .build();

        Order order2 = Order.builder()
                .orderId(2L)
                .orderDetails(Arrays.asList(OrderDetail.builder().orderDetailId(1L)
                        .product(product)
                        .quantity(2)
                        .totalPrice(5000.0)
                        .build()))
                .orderTime(LocalDateTime.now())
                .destinationAddress("TestAddresses2")
                .completed(OrderStatus.INCOMPLETE)
                .user(User.builder().username("User2").build())
                .build();

        //seakan-akan result hasil join ada 2 order sehingga nanti harusnya hasilnya 2 karena 2 uniq
        when(orderRepository.findOrdersAll()).thenReturn(Arrays.asList(order1, order2, order1));

        List<OrderResponse> response = orderService.getOrderAll();

        assertNotNull(response);
        assertEquals(2, response.size());


        response .forEach(order -> {
            if (order.getPembeliName().equals("User1")) {
                assertEquals("TestAddresses1", order.getDestinationAddress());
                assertEquals(OrderStatus.COMPLETE, order.getCompleted());
            } else if (order.getPembeliName().equals("User2")) {
                assertEquals("TestAddresses2", order.getDestinationAddress());
                assertEquals(OrderStatus.INCOMPLETE , order.getCompleted());
            }else{
                throw new RuntimeException("Ada list yang tidak sesuai");
            }
        });

        verify(orderRepository, times(1)).findOrdersAll();
    }

    @Test
    void testGetOrderAllWithEmptyResult() {

        when(orderRepository.findOrdersAll()).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> orderService.getOrderAll());

        verify(orderRepository, times(1)).findOrdersAll();
    }

    //uji coba untuk printpdf

    @Test
    void testPrintOrderExceptionHandling() {
        String username = "NonExistentUser";

        when(orderRepository.findOneOrdersByUserUsername(username, PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));  // Simulate empty result

        assertThrows(ResponseStatusException.class, () -> orderService.printOrder(username));

        verify(orderRepository, times(1)).findOneOrdersByUserUsername(username, PageRequest.of(0, 1));
    }

    @Test
    void testPrintOrder() throws JRException {
        String username = "TestUser";

        Product product = Product.builder()
                .productName("TestProduct")
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .price(2500.0)
                .build();

        Order order1 = Order.builder()
                .orderId(1L)
                .orderTime(LocalDateTime.now())
                .orderDetails(Arrays.asList(OrderDetail.builder().orderDetailId(1L)
                        .product(product)
                        .quantity(2)
                        .totalPrice(5000.0)
                        .build()))
                .destinationAddress("TestAddresses1")
                .completed(OrderStatus.COMPLETE)
                .user(User.builder().username("User1").build())
                .build();

        when(orderRepository.findOneOrdersByUserUsername(eq(username), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(order1)));

        byte[] pdfBytes = orderService.printOrder(username);

        assertNotNull(pdfBytes);

        verify(orderService, Mockito.times(1)).printOrder(username);
        verify(orderRepository, times(1)).findOneOrdersByUserUsername(eq(username), any(Pageable.class));
     }


}