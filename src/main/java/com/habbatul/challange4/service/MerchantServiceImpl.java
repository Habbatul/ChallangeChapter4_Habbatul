package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.enums.MerchantStatus;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.MerchantResponse;
import com.habbatul.challange4.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    MerchantRepository merchantRepository;

    @Override
    public MerchantResponse addMerchant(Merchant merchant) {
        log.debug("Service addMerchant dijalankan");
        if (!merchantRepository.existsByMerchantName(merchant.getMerchantName())) {
            merchantRepository.save(merchant);
            List<MerchantResponse> merchantResponses = toMerchantResponse(Collections.singletonList(merchant));
            log.info("Merchant berhasil ditambahkan : {}", merchant.getMerchantName());
            return merchantResponses.get(0);
        } else {
            log.error("Merchant sudah ada : {}", merchant.getMerchantName());
            throw new CustomException("Merchant is exist!!");
        }
    }

    private List<MerchantResponse> toMerchantResponse(List<Merchant> merchant) {
        log.debug("Response Merchant ditampilkan : size({})", merchant.size());
        return merchant.stream()
                .map(merchants -> MerchantResponse.builder()
                        .merchantName(merchants.getMerchantName())
                        .merchantLocation(merchants.getMerchantLocation())
                        .open(merchants.getOpen().toString())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public MerchantResponse editStatus(Merchant merchant) {
        log.debug("Service editStatus merchant dijalankan");
        Optional<Merchant> merchantByID = merchantRepository.findById(merchant.getMerchantCode());
        if (merchantByID.isPresent()) {
            Merchant oldMerchant = merchantByID.get();
            oldMerchant.setOpen(merchant.getOpen() != null ? merchant.getOpen() : oldMerchant.getOpen());
            merchantRepository.save(oldMerchant);

            List<MerchantResponse> merchantResponses = toMerchantResponse(Collections.singletonList(oldMerchant));
            log.info("Merchant berhasil diupdate : {}", merchant.getMerchantName());
            return merchantResponses.get(0);
        } else {
            log.error("Merchant tidak ditemukan");
            throw new CustomException("Merchant Not found");
        }

    }

    @Override
    public List<MerchantResponse> showOpenMerchant() {
        log.debug("Service showOpenMerchant dijalankan");
        List<Merchant> merchants = merchantRepository.findMerchantByStatus(MerchantStatus.OPEN);

        if (!merchants.isEmpty()) {
            log.info("Berhasil mendapatkan item Merchant");
            return toMerchantResponse(merchants);
        } else {
            log.error("Merchant Kosong");
            throw new CustomException("Merchant tidak ditemukan");
        }

    }
}
