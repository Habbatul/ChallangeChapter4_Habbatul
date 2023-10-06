package com.habbatul.challange4.model;


import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private int quantity;
    private double totalPrice;
    private String productName;
}
