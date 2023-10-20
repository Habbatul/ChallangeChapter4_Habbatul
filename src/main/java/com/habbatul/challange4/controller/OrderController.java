package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Membuat pesanan (berdasarkan header username)")
    @PostMapping(value = "order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<OrderResponse>> createOrder(@RequestHeader String username,
                                                     @RequestBody OrderRequest orderRequest) {

        OrderResponse response = orderService.createOrder(username, orderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                WebResponse.<OrderResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Menampilkan seluruh pesanan")
    @GetMapping(value = "order/admin",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getOrderAll();
        return ResponseEntity.ok(
                WebResponse.<List<OrderResponse>>builder()
                        .data(orderResponses)
                        .build()
        );
    }

    @Operation(summary = "Menampilkan pesanan (berdasarkan header username)")
    @GetMapping(value = "order",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<OrderResponse>>> getUserOrders(@RequestHeader String username) {
        List<OrderResponse> orderResponses = orderService.getOrderByUser(username);
        return ResponseEntity.ok(
                WebResponse.<List<OrderResponse>>builder()
                        .data(orderResponses)
                        .build()
        );
    }

    @Operation(summary = "Melakukan print file pesanan user (berdasarkan header username)")
    @PostMapping (value = "order/print",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> makeOrder(@RequestHeader String username) {

        byte[] pdfBytes = new byte[0];

        try {
            pdfBytes = orderService.printOrder(username);
        } catch (JRException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Maaf ada masalah pada generate file");
        }

        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Order.pdf")
                    .body(pdfBytes);
    }
}
