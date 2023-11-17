package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.OrderService;
import com.habbatul.challange4.utils.AuthExtractor;
import io.swagger.v3.oas.annotations.Operation;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    //untuk dapat username dari jwt
    @Autowired
    private AuthExtractor authExtractor;

    @Autowired
    TaskExecutor asyncTaskExecutor;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Membuat pesanan (berdasarkan username JWTtoken bisa dari cookies atau header Authorization)")
    @PostMapping(value = "order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<OrderResponse>> createOrder(HttpServletRequest request,
                                                                  @RequestBody OrderRequest orderRequest) {

        String username = authExtractor.extractorUsernameFromHeaderCookie(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                WebResponse.<OrderResponse>builder()
                        .data(orderService.createOrder(username, orderRequest))
                        .build()
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Membuat pesanan dengan metode Asynchronous (berdasarkan username JWTtoken bisa dari cookies atau header Authorization)")
    @PostMapping(value = "order/async",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> createOrderAsync(HttpServletRequest request,
                                                                                          @RequestBody OrderRequest orderRequest) {

        CompletableFuture.supplyAsync(() ->
                        authExtractor.extractorUsernameFromHeaderCookie(request), asyncTaskExecutor)
                //compose untuk melanjutkan hasil return dari authExtractor.extractorUsernameFromHeaderCookie
                .thenCompose(username ->
                        orderService.createOrderAsync(username, orderRequest));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<String>builder()
                        .data("Sukses")
                        .build());

    }

    @Operation(summary = "Menampilkan seluruh pesanan (sementara tidak saya hapus)")
    @GetMapping(value = "order/admin",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<OrderResponse>>> getAllOrders() {

        return ResponseEntity.ok(
                WebResponse.<List<OrderResponse>>builder()
                        .data(orderService.getOrderAll())
                        .build()
        );
    }

    @Operation(summary = "Menampilkan pesanan (berdasarkan username JWTtoken bisa dari cookies atau header Authorization)")
    @GetMapping(value = "order",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<OrderResponse>>> getUserOrders(HttpServletRequest request) {
        String username = authExtractor.extractorUsernameFromHeaderCookie(request);

        return ResponseEntity.ok(
                WebResponse.<List<OrderResponse>>builder()
                        .data(orderService.getOrderByUser(username))
                        .build()
        );
    }

    @Operation(summary = "Melakukan print file pesanan user (berdasarkan username JWTtoken bisa dari cookies atau header Authorization)")
    @PostMapping (value = "order/print",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<Object> makeOrder(HttpServletRequest request) throws JRException {
        String username = authExtractor.extractorUsernameFromHeaderCookie(request);

        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=Order.pdf")
                    .body(orderService.printOrder(username));
    }
}
