package com.habbatul.challange4.service;


import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderResponse;
import net.sf.jasperreports.engine.JRException;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(String username, OrderRequest order);
    List<OrderResponse> getOrderByUser(String username);
    List<OrderResponse> getOrderAll();

    byte[] printOrder(String username) throws JRException;
}
