package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.enums.MerchantStatus;
import com.habbatul.challange4.model.requests.CreateMerchantRequest;
import com.habbatul.challange4.model.requests.UpdateMerchantRequest;
import com.habbatul.challange4.model.responses.MerchantResponse;
import com.habbatul.challange4.repository.MerchantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MerchantServiceTest {

    @Spy
    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Mock
    private MerchantRepository merchantRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        Mockito.reset(merchantRepository);
    }


    @Test
    public void testAddMerchantAsync() {
        CreateMerchantRequest request = new CreateMerchantRequest();
        request.setMerchantName("TestMerchant");
        request.setMerchantLocation("TestLocation");
        request.setOpen(MerchantStatus.OPEN);

        //methdo async
        CompletableFuture<MerchantResponse> futureMerchant = merchantService.addMerchantAsync(request);

        //memastikan eksekusi tidak diblokir
        assertFalse(futureMerchant.isDone());

        //tunggu operasi asynchronous (gunakan metode join untuk menunggu selesai)
        MerchantResponse merchantResponse = futureMerchant.join();

        assertTrue(futureMerchant.isDone());
        assertNotNull(merchantResponse);
    }

    @Test
    void testAddMerchant() {
        CreateMerchantRequest request = CreateMerchantRequest.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.OPEN)
                .build();

        when(merchantRepository.existsByMerchantName("TestMerchant")).thenReturn(false);

        Merchant savedMerchant = Merchant.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.OPEN)
                .build();
        when(merchantRepository.save(any())).thenReturn(savedMerchant);

        MerchantResponse response = merchantService.addMerchant(request);

        assertNotNull(response);
        assertEquals("TestMerchant", response.getMerchantName());
        assertEquals("TestLocation", response.getMerchantLocation());
        assertEquals(MerchantStatus.OPEN, response.getOpen());

        verify(merchantRepository, times(1)).existsByMerchantName("TestMerchant");
    }

    @Test
    void testAddMerchantWhenMerchantExists() {
        CreateMerchantRequest request = CreateMerchantRequest.builder()
                .merchantName("TestMerchant")
                .merchantLocation("TestLocation")
                .open(MerchantStatus.OPEN)
                .build();

        when(merchantRepository.existsByMerchantName("TestMerchant")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> merchantService.addMerchant(request));

        verify(merchantRepository, times(1)).existsByMerchantName("TestMerchant");
        verify(merchantRepository, never()).save(any());
    }

    @Test
    void testEditStatus() {
        UpdateMerchantRequest request = UpdateMerchantRequest.builder()
                .open(MerchantStatus.CLOSED)
                .build();

        Merchant existingMerchant = Merchant.builder()
                .merchantName("TestMerchant")
                .merchantLocation("OriginalLocation")
                .open(MerchantStatus.OPEN)
                .build();

        when(merchantRepository.findByMerchantName("TestMerchant")).thenReturn(Optional.of(existingMerchant));


        MerchantResponse response = merchantService.editStatus("TestMerchant", request);

        assertNotNull(response);
        assertEquals("TestMerchant", response.getMerchantName());
        assertEquals(MerchantStatus.CLOSED, response.getOpen());

        verify(merchantRepository, times(1)).findByMerchantName("TestMerchant");
    }

    @Test
    void testEditStatusWhenMerchantNotFound() {
        UpdateMerchantRequest request = UpdateMerchantRequest.builder()
                .open(MerchantStatus.CLOSED)
                .build();

        when(merchantRepository.findByMerchantName("TestMerchant")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> merchantService.editStatus("TestMerchant", request));

        verify(merchantRepository, times(1)).findByMerchantName("TestMerchant");
        verify(merchantRepository, never()).save(any());
    }

    @Test
    void testShowOpenMerchant() {
        Merchant openMerchant = Merchant.builder()
                .merchantName("OpenMerchant")
                .merchantLocation("Location1")
                .open(MerchantStatus.OPEN)
                .build();

        when(merchantRepository.findMerchantByStatus(MerchantStatus.OPEN)).thenReturn(Collections.singletonList(openMerchant));

        List<MerchantResponse> response = merchantService.showOpenMerchant();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("OpenMerchant", response.get(0).getMerchantName());
        assertEquals("Location1", response.get(0).getMerchantLocation());
        assertEquals(MerchantStatus.OPEN, response.get(0).getOpen());

        verify(merchantRepository, times(1)).findMerchantByStatus(MerchantStatus.OPEN);
    }

    @Test
    void testShowOpenMerchantWhenOpenMerchantNotFound() {
        when(merchantRepository.findMerchantByStatus(MerchantStatus.OPEN)).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> merchantService.showOpenMerchant());

        verify(merchantRepository, times(1)).findMerchantByStatus(MerchantStatus.OPEN);
    }
}
