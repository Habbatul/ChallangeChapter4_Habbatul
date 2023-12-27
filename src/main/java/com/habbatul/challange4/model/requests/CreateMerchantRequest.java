package com.habbatul.challange4.model.requests;

import com.habbatul.challange4.enums.MerchantStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantRequest {

    private String merchantName;

    private String merchantLocation;

    private MerchantStatus open;
}
