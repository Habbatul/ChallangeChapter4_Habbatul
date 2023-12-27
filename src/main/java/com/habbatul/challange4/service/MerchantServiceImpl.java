package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.enums.MerchantStatus;
import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;
import com.habbatul.challange4.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    TaskExecutor asyncTaskExecutor;

    @Async("asyncTaskExecutor")
    @Transactional
    @Override
    public CompletableFuture<MerchantResponse> addMerchantAsync(CreateMerchantRequest createMerchantRequest) {
        return CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
           log.info("ini running pada thread : {}", Thread.currentThread().getName());
           return this.addMerchant(createMerchantRequest);
        }, asyncTaskExecutor);
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @Override
    public CompletableFuture<MerchantResponse> editMerchantAsync(String merchantName, UpdateMerchantRequest updateMerchantRequest) {
        return CompletableFuture.supplyAsync(() ->{
                log.info("ini running pada thread : {}", Thread.currentThread().getName());
                return this.editStatus(merchantName, updateMerchantRequest);
        }, asyncTaskExecutor);
    }

    @Transactional
    @Override
    public MerchantResponse addMerchant(CreateMerchantRequest createMerchantRequest) {
        //process request
        Merchant merchant = Merchant.builder()
                .merchantName(createMerchantRequest.getMerchantName())
                .merchantLocation(createMerchantRequest.getMerchantLocation())
                .open(createMerchantRequest.getOpen())
                .build();

        log.debug("Service addMerchant dijalankan");
        if (!merchantRepository.existsByMerchantName(merchant.getMerchantName())) {
            merchantRepository.save(merchant);
            List<MerchantResponse> merchantResponses = toMerchantResponse(Collections.singletonList(merchant));
            log.info("Merchant berhasil ditambahkan : {}", merchant.getMerchantName());
            return merchantResponses.get(0);
        } else {
            log.error("Merchant sudah ada : {}", merchant.getMerchantName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Merchant sudah didaftarkan sebelumnya");
        }
    }

    private List<MerchantResponse> toMerchantResponse(List<Merchant> merchant) {
        log.debug("Response Merchant ditampilkan : size({})", merchant.size());
        return merchant.stream()
                .map(merchants -> MerchantResponse.builder()
                        .merchantName(merchants.getMerchantName())
                        .merchantLocation(merchants.getMerchantLocation())
                        .open(merchants.getOpen())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MerchantResponse editStatus(String merchantName, UpdateMerchantRequest updateMerchantRequest) {
        //process request
        Merchant merchant = new Merchant();
        merchant.setMerchantName(merchantName);
        merchant.setOpen(updateMerchantRequest.getOpen());

        log.debug("Service editStatus merchant dijalankan");
        Optional<Merchant> merchantByID = merchantRepository.findByMerchantName(merchant.getMerchantName());
        if (merchantByID.isPresent()) {
            Merchant oldMerchant = merchantByID.get();
            oldMerchant.setOpen(merchant.getOpen() != null ? merchant.getOpen() : oldMerchant.getOpen());
            merchantRepository.save(oldMerchant);

            List<MerchantResponse> merchantResponses = toMerchantResponse(Collections.singletonList(oldMerchant));
            log.info("Merchant berhasil diupdate : {}", merchant.getMerchantName());
            return merchantResponses.get(0);
        } else {
            log.error("Merchant tidak ditemukan.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Merchant tidak ditemukan");
        }

    }

    @Transactional(readOnly = true)
    @Override
    public List<MerchantResponse> showOpenMerchant() {
        log.debug("Service showOpenMerchant dijalankan");
        List<Merchant> merchants = merchantRepository.findMerchantByStatus(MerchantStatus.OPEN);

        if (!merchants.isEmpty()) {
            log.info("Berhasil mendapatkan item Merchant");
            return toMerchantResponse(merchants);
        } else {
            log.error("Merchant Kosong");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Merchant tidak ditemukan");
        }

    }
}
