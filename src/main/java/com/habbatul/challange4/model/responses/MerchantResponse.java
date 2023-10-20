package com.habbatul.challange4.model.responses;


import com.habbatul.challange4.enums.MerchantStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class MerchantResponse {
    private String merchantName;
    private String merchantLocation;
    private MerchantStatus open;
}
