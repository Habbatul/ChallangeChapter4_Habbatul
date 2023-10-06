package com.habbatul.challange4;

import com.habbatul.challange4.entity.*;
import com.habbatul.challange4.enums.OrderStatus;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.OrderDetailResponse;
import com.habbatul.challange4.model.OrderResponse;
import com.habbatul.challange4.repository.OrderDetailRepository;
import com.habbatul.challange4.repository.OrderRepository;
import com.habbatul.challange4.repository.ProductRepository;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        orderDetailRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateOrder() {
        User user = User.builder()
                .username("TestUser")
                .build();
        userRepository.save(user);

        Product product = Product.builder()
                .productName("TestProduct")
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .price(100.0)
                .build();
        productRepository.save(product);

        Product product2 = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct2")
                .price(200.0)
                .build();
        productRepository.save(product2);


        Order order = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2 * product.getPrice())
                .order(order)
                .build();

        //buat 2 order yang memiliki product yang sama untuk test merge func
        OrderDetail orderDetail2 = OrderDetail.builder()
                .product(product2)
                .quantity(1)
                .totalPrice(1 * product2.getPrice())
                .order(order)
                .build();

        OrderDetail orderDetail3 = OrderDetail.builder()
                .product(product2)
                .quantity(2)
                .totalPrice(2 * product2.getPrice())
                .order(order)
                .build();

        //gunakan service
        OrderResponse response = orderService.createOrder(
                order, Arrays.asList(orderDetail, orderDetail2, orderDetail3), user
        );

        //cek response order
        assertNotNull(response);
        assertEquals(order.getOrderTime(), response.getOrderTime());
        assertEquals("TestAddresses", response.getDestinationAddress());
        assertEquals(order.getCompleted().toString(), response.getCompleted());
        assertEquals(user.getUsername(), response.getPembeliName());

        //cek response detail order
        assertEquals(2, response.getDetailOrder().size());

        List<OrderDetailResponse> orderDetails = response.getDetailOrder();

        //Cek untuk tiap detail produk menggunakan productName, karena itu uniq
        orderDetails.forEach(detail -> {
            if (detail.getProductName().equals("TestProduct2")) {
                assertEquals(3, detail.getQuantity());
                assertEquals(3 * product2.getPrice(), detail.getTotalPrice());
            } else if (detail.getProductName().equals("TestProduct")) {
                assertEquals(2, detail.getQuantity());
                assertEquals(2 * product.getPrice(), detail.getTotalPrice());
            }
        });

        Optional<OrderDetail> orderDetail1 = orderDetailRepository.findById(orderDetail.getOrderDetailId());
        assertEquals(orderDetail.getOrder().getOrderId(), orderDetail1.get().getOrder().getOrderId());
    }


    @Test
    void testGetOrderByUser() {
        User user = User.builder()
                .username("TestUser")
                .build();
        userRepository.save(user);
        Product product = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct")
                .price(100.0)
                .build();
        productRepository.save(product);


        Order order = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();
        orderRepository.save(order);

        Order order2 = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now(ZoneId.of("America/New_York")).truncatedTo(ChronoUnit.MICROS))
                .destinationAddress("TestAddresses2")
                .completed(OrderStatus.INCOMPLETE)
                .build();
        orderRepository.save(order2);

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2 * product.getPrice())
                .order(order)
                .build();
        orderDetailRepository.save(orderDetail);

        OrderDetail orderDetail2 = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2 * product.getPrice())
                .order(order2)
                .build();
        orderDetailRepository.save(orderDetail2);

        order.setOrderDetails(Collections.singletonList(orderDetail));
        order2.setOrderDetails(Collections.singletonList(orderDetail2));

        //jalankan service
        List<OrderResponse> responses = orderService.getOrderByUser(user);

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    void testGetOrderByUserNotFound() {
        User user = User.builder()
                .username("TestUser")
                .build();
        userRepository.save(user);

        //jalankan service
        assertThrows(CustomException.class, () -> orderService.getOrderByUser(user));
    }


    @Test
    void testGetOrderAll() {
        User user = User.builder()
                .username("TestUser")
                .build();
        userRepository.save(user);
        Product product = Product.builder()
                .productName("TestProduct")
                .price(100.0)
                .build();
        productRepository.save(product);


        Order order = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();
        orderRepository.save(order);

        Order order2 = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now(ZoneId.of("America/New_York")).truncatedTo(ChronoUnit.MICROS))
                .destinationAddress("TestAddresses2")
                .completed(OrderStatus.INCOMPLETE)
                .build();
        orderRepository.save(order2);

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2 * product.getPrice())
                .order(order)
                .build();
        orderDetailRepository.save(orderDetail);

        OrderDetail orderDetail2 = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2 * product.getPrice())
                .order(order2)
                .build();
        orderDetailRepository.save(orderDetail2);

        order.setOrderDetails(Collections.singletonList(orderDetail));
        order2.setOrderDetails(Collections.singletonList(orderDetail2));

        List<OrderResponse> responses = orderService.getOrderAll();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        //cek isian response nya
        responses.forEach(orders -> {
            List<OrderDetailResponse> detailOrders = orders.getDetailOrder();
            detailOrders.forEach(detailOrder -> {
                assertTrue(
                        orderDetail2.getProduct().getProductName().equals(detailOrder.getProductName()) ||
                                orderDetail.getProduct().getProductName().equals(detailOrder.getProductName()),
                        "Ada kesalahan pada response DetailOrder.ProductName"
                );
                assertTrue(
                        orderDetail2.getQuantity().equals(detailOrder.getQuantity()) ||
                                orderDetail.getQuantity().equals(detailOrder.getQuantity()),
                        "Ada kesalahan pada response DetailOrder.Quantity"
                );
                assertTrue(
                        orderDetail2.getTotalPrice().equals(detailOrder.getTotalPrice()) ||
                                orderDetail.getTotalPrice().equals(detailOrder.getTotalPrice()),
                        "Ada kesalahan pada response DetailOrder.TotalPrice"
                );
            });
            assertTrue(
                    order.getOrderTime().equals(orders.getOrderTime()) ||
                            order2.getOrderTime().equals(orders.getOrderTime()),
                    "Ada kesalahan pada response Order.OrderTime"
            );
            assertTrue(
                    order.getCompleted().toString().equals(orders.getCompleted()) ||
                            order2.getCompleted().toString().equals(orders.getCompleted()),
                    "Ada kesalahan pada response Order.StatusCompleted"
            );
            assertTrue(
                    order.getDestinationAddress().equals(orders.getDestinationAddress()) ||
                            order2.getDestinationAddress().equals(orders.getDestinationAddress()),
                    "Ada kesalahan pada response Order.Destinasi_Address"
            );
            assertTrue(
                    order.getUser().getUsername().equals(orders.getPembeliName()) ||
                            order2.getUser().getUsername().equals(orders.getPembeliName()),
                    "Ada kesalahan pada response Order.nama_pembeli"
            );

        });
    }

    @Test
    void testGetOrderAllNotFound() {
        assertThrows(CustomException.class, () -> orderService.getOrderAll());
    }

    @Test
    void testDuplicateFromJoin() {
        User user = User.builder()
                .username("TestUser")
                .build();
        userRepository.save(user);

        Product product = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct")
                .price(100.0)
                .build();
        productRepository.save(product);

        Product product2 = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct2")
                .price(200.0)
                .build();
        productRepository.save(product2);


        Order order = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .destinationAddress("TestAddresses")
                .completed(OrderStatus.COMPLETE)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2 * product.getPrice())
                .order(order)
                .build();

        //buat 2 order yang memiliki product yang sama untuk test merge func
        OrderDetail orderDetail2 = OrderDetail.builder()
                .product(product2)
                .quantity(1)
                .totalPrice(1 * product2.getPrice())
                .order(order)
                .build();

        OrderDetail orderDetail3 = OrderDetail.builder()
                .product(product2)
                .quantity(2)
                .totalPrice(2 * product2.getPrice())
                .order(order)
                .build();

        //gunakan service
        orderService.createOrder(
                order, Arrays.asList(orderDetail, orderDetail2, orderDetail3), user
        );

        List<OrderResponse> responses = orderService.getOrderAll();
        List<OrderResponse> responses2 = orderService.getOrderByUser(user);
        List<OrderDetailResponse> detailResponses = responses.stream().flatMap(response ->
                response.getDetailOrder().stream()).collect(Collectors.toList());
        List<OrderDetailResponse> detailResponses2 = responses2.stream().flatMap(response ->
                response.getDetailOrder().stream()).collect(Collectors.toList());


        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1, responses2.size());
        assertEquals(2, detailResponses.size());
        assertEquals(2, detailResponses2.size());

        //cek isian response nya
        responses.forEach(orders -> {
            List<OrderDetailResponse> detailOrders = orders.getDetailOrder();
            detailOrders.forEach(detailOrder -> {
                assertTrue(
                        orderDetail2.getProduct().getProductName().equals(detailOrder.getProductName()) ||
                                orderDetail.getProduct().getProductName().equals(detailOrder.getProductName()) ||
                                orderDetail3.getQuantity().equals(detailOrder.getQuantity()),
                        "Ada kesalahan pada response DetailOrder.ProductName"
                );
                assertTrue(
                        orderDetail2.getQuantity().equals(detailOrder.getQuantity()) ||
                                orderDetail.getQuantity().equals(detailOrder.getQuantity()) ||
                                orderDetail3.getQuantity().equals(detailOrder.getQuantity()),
                        "Ada kesalahan pada response DetailOrder.Quantity"
                );
                assertTrue(
                        orderDetail2.getTotalPrice().equals(detailOrder.getTotalPrice()) ||
                                orderDetail.getTotalPrice().equals(detailOrder.getTotalPrice()) ||
                                orderDetail3.getQuantity().equals(detailOrder.getQuantity()),
                        "Ada kesalahan pada response DetailOrder.TotalPrice"
                );
            });
            assertEquals(order.getOrderTime(), orders.getOrderTime(),
                    "Ada kesalahan pada response Time");
            assertEquals(order.getCompleted().toString(), orders.getCompleted(),
                    "Ada kesalahan pada response Complete");
            assertEquals(order.getDestinationAddress(), orders.getDestinationAddress(),
                    "Ada kesalahan pada response DestinationAddress");
            assertEquals(order.getUser().getUsername(), orders.getPembeliName(),
                    "Ada kesalahan pada response PembeliName");

        });

    }


}

