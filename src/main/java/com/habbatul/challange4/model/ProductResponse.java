package com.habbatul.challange4.model;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String productName;
    private double price;
    private String merchantName;
}
