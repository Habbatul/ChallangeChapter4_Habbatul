package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class MerchantController {

    @Autowired
    MerchantService merchantService;

    @Operation(summary = "Menampilkan merchant yang sedang buka")
    @GetMapping(
            value = "/merchant",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<MerchantResponse>>> showOpenMerchants() {

            List<MerchantResponse> openMerchants = merchantService.showOpenMerchant();

            WebResponse<List<MerchantResponse>> response = WebResponse.<List<MerchantResponse>>builder()
                    .data(openMerchants)
                    .build();

            return ResponseEntity.ok(response);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Menambahkan merchant")
    @PostMapping(
            value = "/merchant",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MerchantResponse>> createMerchant(
            @RequestBody CreateMerchantRequest createMerchantRequest) {

            MerchantResponse response = merchantService.addMerchant(createMerchantRequest);
            WebResponse<MerchantResponse> merchResponse = WebResponse.<MerchantResponse>builder()
                .data(response)
                .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(merchResponse);
    }


    @Operation(summary = "Edit status merchant buka/tutup")
    @PutMapping(
            value = "/merchant/{merchantName}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<MerchantResponse>> editMerchant(
            @PathVariable String merchantName,
            @RequestBody UpdateMerchantRequest updateMerchantRequest) {

        MerchantResponse response = merchantService.editStatus(merchantName, updateMerchantRequest);
        WebResponse<MerchantResponse> merchResponse = WebResponse.<MerchantResponse>builder()
                .data(response)
                .build();

        return ResponseEntity.ok().body(merchResponse);
    }



}
