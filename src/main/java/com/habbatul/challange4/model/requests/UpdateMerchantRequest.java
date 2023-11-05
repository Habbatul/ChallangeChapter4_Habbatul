package com.habbatul.challange4.model.requests;

import com.habbatul.challange4.enums.MerchantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMerchantRequest {
    private String merchantLocation;
    private MerchantStatus open;
}
