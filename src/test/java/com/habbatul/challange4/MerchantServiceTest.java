package com.habbatul.challange4;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.enums.MerchantStatus;
import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;
import com.habbatul.challange4.repository.MerchantRepository;
import com.habbatul.challange4.service.MerchantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MerchantServiceTest {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantRepository merchantRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        merchantRepository.deleteAll();
    }


    @Test
    void testAddMerchant() {
        CreateMerchantRequest merchant = CreateMerchantRequest.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.OPEN)
                .build();

        MerchantResponse response = merchantService.addMerchant(merchant);

        assertNotNull(response);
        assertEquals("TestMerchant", response.getMerchantName());
        assertEquals("TestLocation", response.getMerchantLocation());
        assertEquals("OPEN", response.getOpen());
    }

    @Test
    void testAddMerchantExist() {
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.OPEN)
                .build();
        merchantRepository.save(merchant);
        CreateMerchantRequest merchant2 = CreateMerchantRequest.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.OPEN)
                .build();

        assertThrows(ResponseStatusException.class, () -> merchantService.addMerchant(merchant2));
    }

    @Test
    void testEditStatus() {
        // Create a merchant
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.CLOSED)
                .build();
        merchantRepository.save(merchant);

        // Change merchant status
        UpdateMerchantRequest merchantReq = UpdateMerchantRequest.builder()
                .open(MerchantStatus.OPEN)
                .build();

        MerchantResponse response = merchantService.editStatus(merchant.getMerchantName() ,merchantReq);

        assertNotNull(response);
        assertEquals("OPEN", response.getOpen());
    }

    @Test
    void testEditNotFound() {
        UpdateMerchantRequest merchant = UpdateMerchantRequest.builder()
                .build();
        String merchantName = "ERROR";

        assertThrows(ResponseStatusException.class, () -> merchantService.editStatus(merchantName, merchant));
    }


    @Test
    void testShowMerchant() {
        // Create open and closed merchants
        Merchant openMerchant = Merchant.builder()
                .merchantName("OpenMerchant")
                .merchantLocation("OpenLocation")
                .open(MerchantStatus.OPEN)
                .build();
        merchantRepository.save(openMerchant);

        Merchant closedMerchant = Merchant.builder()
                .merchantName("ClosedMerchant")
                .merchantLocation("ClosedLocation")
                .open(MerchantStatus.CLOSED)
                .build();
        merchantRepository.save(closedMerchant);

        List<MerchantResponse> responses = merchantService.showOpenMerchant();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("OPEN", responses.get(0).getOpen());
    }

    @Test
    void testShowNotFound() {
        Merchant closedMerchant = Merchant.builder()
                .merchantName("ClosedMerchant")
                .merchantLocation("ClosedLocation")
                .open(MerchantStatus.CLOSED)
                .build();
        merchantRepository.save(closedMerchant);

        assertThrows(ResponseStatusException.class, () -> merchantService.showOpenMerchant());
    }

}
