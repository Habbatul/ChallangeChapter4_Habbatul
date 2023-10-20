package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Order;
import com.habbatul.challange4.entity.OrderDetail;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.enums.OrderStatus;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.requests.OrderDetailRequest;
import com.habbatul.challange4.model.requests.OrderRequest;
import com.habbatul.challange4.model.responses.OrderDetailResponse;
import com.habbatul.challange4.model.responses.OrderResponse;
import com.habbatul.challange4.repository.OrderDetailRepository;
import com.habbatul.challange4.repository.OrderRepository;
import com.habbatul.challange4.repository.ProductRepository;
import com.habbatul.challange4.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    //  public OrderResponse createOrder(Order order, List<OrderDetail> orderDetail, User user)
    @Override
    public OrderResponse createOrder(String username, OrderRequest orderReq) {


        log.debug("Service createOrder dijalankan");

        //buat order tanpa order detail
        Order order = Order.builder()
                .user(
                        userRepository.findUserByUsername(username).orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.BAD_REQUEST, "User tidak ada"))
                )
                .orderTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .destinationAddress(orderReq.getDestinationAddress())
                .completed(orderReq.getCompleted())
                .build();

        //buat order detailnya
        List<OrderDetail> orderDetail = convertToOrderDetail(orderReq.getDetailOrder());
        orderDetail.forEach(orderDetails -> orderDetails.setOrder(order));

        List<OrderDetail> orderDetailFinalisasi = new ArrayList<>(orderDetail.stream()
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

        //set orderDetail ke order
        order.setOrderDetails(orderDetailFinalisasi);

        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetailFinalisasi);
        List<OrderResponse> orderResponses = toOrderResponse(Collections.singletonList(order));
        return orderResponses.get(0); //karena hanya dapat menambah satu per service, index pertama
    }


    private List<OrderDetail> convertToOrderDetail(List<OrderDetailRequest> orderDetailResponses) {
        return orderDetailResponses.stream()
                .map(orderDetailResponse -> {
                    Product product = productRepository.
                            findByProductName(orderDetailResponse.getProductName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Product Tidak ada"));

                    return OrderDetail.builder()
                            .product(product)
                            .quantity(orderDetailResponse.getQuantity())
                            .totalPrice(product.getPrice() * orderDetailResponse.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
    }


    private List<OrderResponse> toOrderResponse(List<Order> orders) {
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
                            .completed(order.getCompleted())
                            .destinationAddress(order.getDestinationAddress())
                            .pembeliName(order.getUser().getUsername())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getOrderByUser(String username) {
        log.debug("Service getOrderByUser dijalankan");

        List<Order> orders = orderRepository.findOrdersByUserUsername(username);
        if (orders.isEmpty()) {
            log.error("Order pada tabel kosong");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username tidak terdaftar atau belum membuat order");
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


    @Override
    @Transactional(readOnly = true)
    public byte[] printOrder(String username) throws JRException {
        log.debug("Service printOrder dijalankan");

        //Penerapan untuk melimit satu saja data yang diambil
        Page<Order> orderPage = orderRepository.findOneOrdersByUserUsername(username, PageRequest.of(0, 1));
        if (orderPage.getContent().isEmpty() || orderPage.getContent().get(0) == null) {
            log.info("Username tidak terdaftar atau belum memesan, username : {}", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username tidak terdaftar atau belum memesan");
        }
        Order order = orderPage.getContent().get(0);

        List<OrderDetailResponse> orderDetailList = order.getOrderDetails().stream()
                .map(orderDetail -> {
                    Product product = orderDetail.getProduct();
                    return OrderDetailResponse.builder()
                            .totalPrice(orderDetail.getTotalPrice())
                            .quantity(orderDetail.getQuantity())
                            .productName(product.getProductName())
                            .build();
                })
                .collect(Collectors.toList());

        String usernames = order.getUser().getUsername();
        String destinationAddress = order.getDestinationAddress();

        Double totalOrderPrice = orderDetailList.stream().mapToDouble(OrderDetailResponse::getTotalPrice).sum();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", usernames);
        parameters.put("destinationAddress", destinationAddress);
        parameters.put("totalOrderPrice", totalOrderPrice);

        //tambahan baru untuk parameter directory gambar, sebelumnya langsung bisa tapi untuk dijadikan byte tidak memungkinkan langsung bila pakai JasperExportManager.exportReportToPdf(jasperPrint); harus pakai JasperExportManager.exportReportToPdfFile
        parameters.put("bgDirectory", getClass().getResourceAsStream("/tulisan.png"));


        InputStream jrxmlInputStream = getClass().getResourceAsStream("/makeOrder.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInputStream);


        //Jasperprint digunakan untuk generating file serta melakukan transfer ke parameter (baik datasource/mapParameter)
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                new JRBeanCollectionDataSource(orderDetailList));

        //cara ini berarti kembalian nya byte[] nanti dikonversi pakek ContentType("application/pdf") & Header("Content-Disposition", "attachment; filename=StructOrder.pdf");
        byte[] jaspper = JasperExportManager.exportReportToPdf(jasperPrint);
        return jaspper;

    }
}


//pada diatas klo percobaan response endpoint pdf gagal pakai lagi ubah controller jadi kembalian json lagi
////        Ingat Ubah kembali fungsi menjadi kembalian string kalo pakek cara simple
////        terlalu pakem variableny (cara simple)
//        String path = "C:\\Users\\Axioo Pongo\\Downloads";
//        String pdfPath = path + "\\Order.pdf";
////        Export ke pdf kalo pakek yang variable pakem tadi untuk generate file (cara simple)
//        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);
//        return "Berhasil menyimpan pada path "+pdfPath";

