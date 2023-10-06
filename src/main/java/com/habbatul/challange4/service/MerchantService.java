package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.model.MerchantResponse;

import java.util.List;

public interface MerchantService {
    MerchantResponse addMerchant(Merchant merchant);
    MerchantResponse editStatus(Merchant merchant);

    List<MerchantResponse> showOpenMerchant();
}
