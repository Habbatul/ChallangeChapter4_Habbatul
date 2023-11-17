package com.habbatul.challange4.service;


import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderResponse;
import net.sf.jasperreports.engine.JRException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    //untuk async
    CompletableFuture<OrderResponse> createOrderAsync(String username, OrderRequest orderReq);
    OrderResponse createOrder(String username, OrderRequest order);
    List<OrderResponse> getOrderByUser(String username);
    List<OrderResponse> getOrderAll();

    byte[] printOrder(String username) throws JRException;
}
