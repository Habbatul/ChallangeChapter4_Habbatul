package com.habbatul.challange4.service;

import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;

import java.util.List;

public interface MerchantService {
    MerchantResponse addMerchant(CreateMerchantRequest createMerchantRequest);
    MerchantResponse editStatus(String merchantName, UpdateMerchantRequest updateMerchantRequest);

    List<MerchantResponse> showOpenMerchant();
}
