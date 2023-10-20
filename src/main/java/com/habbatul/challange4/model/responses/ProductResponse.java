package com.habbatul.challange4.model.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String productCode;
    private String productName;
    private double price;
    private String merchantName;
}
