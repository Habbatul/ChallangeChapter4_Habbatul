package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Order;
import com.habbatul.challange4.entity.OrderDetail;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.model.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(Order order, List<OrderDetail> orderDetail, User user);
    List<OrderResponse> getOrderByUser(User user);
    List<OrderResponse> getOrderAll();
}
