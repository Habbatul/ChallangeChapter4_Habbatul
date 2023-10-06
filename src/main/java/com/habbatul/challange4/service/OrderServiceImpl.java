package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Order;
import com.habbatul.challange4.entity.OrderDetail;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.OrderDetailResponse;
import com.habbatul.challange4.model.OrderResponse;
import com.habbatul.challange4.repository.OrderDetailRepository;
import com.habbatul.challange4.repository.OrderRepository;
import com.habbatul.challange4.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public OrderResponse createOrder(Order order, List<OrderDetail> orderDetail, User user) {
        log.debug("Service createOrder dijalankan");

        orderRepository.save(order);

        List<OrderDetail> orderDetailFinalisasi = new ArrayList<>(orderDetail.stream()

                /*
                kumpulkan elemen stream ke map lalu nanti kalo ada konflik dua order detail
                dengan kunci yang sama maka gabungkan tapi perbarui quantity dan totalprice nya.
                existingOrderdetail untuk penanda sudah ada di map, newOrder sebagai isi yang
                baru (untuk digbungkan nantinya diambil nilainya untuk ditambahkan ke existing
                */

                .collect(Collectors.toMap(
                        OrderDetail::getProduct, //key nya
                        orderDetails -> orderDetails, //value
                        (existingOrderDetail, newOrderDetail) -> { //merge nya

                            existingOrderDetail.setQuantity(
                                    existingOrderDetail.getQuantity() + newOrderDetail.getQuantity()
                            );

                            existingOrderDetail.setTotalPrice(
                                    existingOrderDetail.getTotalPrice() + newOrderDetail.getTotalPrice()
                            );

                            return existingOrderDetail;
                        }
                )).values());

        orderDetailRepository.saveAll(orderDetailFinalisasi);
        log.info("Order dan Detail Order berhasil disimpan : OrderTime({})", order.getOrderTime());

        order.setOrderDetails(new ArrayList<>(orderDetailFinalisasi));

        List<OrderResponse> orderResponses = toOrderResponse(Collections.singletonList(order));
        return orderResponses.get(0); //karena hanya dapat menambah satu per service, index pertama
    }


    private List<OrderResponse> toOrderResponse(List<Order> orders) {
        log.debug("Response orders ditampilkan : size({})", orders.size());
        return orders.stream()
                .map(order -> {
                    List<OrderDetailResponse> tempOrderDetail = order.getOrderDetails().stream()
                            .map(orderDetail -> {
                                Product product = orderDetail.getProduct();
                                return OrderDetailResponse.builder()
                                        .quantity(orderDetail.getQuantity())
                                        .totalPrice(orderDetail.getTotalPrice())
                                        .productName(product.getProductName())
                                        .build();

                            })
                            .collect(Collectors.toList());

                    //kalo list order detail udah dapet masukkan lalu return dto
                    return OrderResponse.builder()
                            .detailOrder(tempOrderDetail)
                            .orderTime(order.getOrderTime())
                            .completed(order.getCompleted().toString())
                            .destinationAddress(order.getDestinationAddress())
                            .pembeliName(order.getUser().getUsername())
                            .build();
                })
                .collect(Collectors.toList());
    }

    //izin kak, dia error :
    //org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role
    //saya cari solusi suruh pakek Transactional
    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getOrderByUser(User user) {
        log.debug("Service getOrderByUser dijalankan");

        List<Order> orders = orderRepository.findOrdersByUserId(user.getUserId());
        if (orders.isEmpty()) {
            log.error("Order pada tabel kosong");
            throw new CustomException("Order Tidak ditemukan");
        } else {
            log.info("Item order berhasil didapatkan");
        }

        List<Order> uniqueOrders = new ArrayList<>(orders.stream()
                //Membuat perulangan hanya sekali bila id_order sama
                .collect(Collectors.toMap(Order::getOrderId, order -> order,
                        (existing, replacement) -> existing))
                .values());

        return toOrderResponse(uniqueOrders);
    }

    //izin kak, dia error :
    //org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role
    //saya cari solusi suruh pakek Transactional
    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getOrderAll() {
        log.debug("Service getOrderAll dijalankan");

        List<Order> orders = orderRepository.findOrdersAll();

        if (orders.isEmpty()) {
            log.error("Order pada tabel kosong");
            throw new CustomException("Order Tidak ditemukan");
        } else {
            log.info("Item order berhasil didapatkan");
        }

        //mencegah hasil redundant order dari hasil join dengan order_detail
        List<Order> uniqueOrders = new ArrayList<>(orders.stream()
                //Membuat perulangan hanya sekali bila id_order sama
                .collect(Collectors.toMap(Order::getOrderId, order -> order,
                        (existing, replacement) -> existing))
                .values());
        return toOrderResponse(uniqueOrders);
    }

}
