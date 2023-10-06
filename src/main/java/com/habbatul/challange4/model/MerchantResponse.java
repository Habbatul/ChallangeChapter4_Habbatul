package com.habbatul.challange4.model;


import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class MerchantResponse {
    private String merchantName;
    private String merchantLocation;
    private String open;
}
