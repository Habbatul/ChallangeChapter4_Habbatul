package com.habbatul.challange4.service;

import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MerchantService {
    //untuk async nya
    CompletableFuture<MerchantResponse> addMerchantAsync(CreateMerchantRequest createMerchantRequest);
    CompletableFuture<MerchantResponse> editMerchantAsync(String merchantName, UpdateMerchantRequest updateMerchantRequest);

    MerchantResponse addMerchant(CreateMerchantRequest createMerchantRequest);
    MerchantResponse editStatus(String merchantName, UpdateMerchantRequest updateMerchantRequest);

    List<MerchantResponse> showOpenMerchant();
}
