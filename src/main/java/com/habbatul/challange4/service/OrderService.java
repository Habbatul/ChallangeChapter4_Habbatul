package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Order;
import com.habbatul.challange4.entity.OrderDetail;
import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderResponse;
import net.sf.jasperreports.engine.JRException;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(String username, OrderRequest order);
    List<OrderResponse> getOrderByUser(String username);
    List<OrderResponse> getOrderAll();

    byte[] printOrder(String username) throws JRException;
}
