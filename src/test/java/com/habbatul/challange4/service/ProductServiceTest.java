package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.model.requests.CreateProductRequest;
import com.habbatul.challange4.model.requests.UpdateProductRequest;
import com.habbatul.challange4.model.responses.ProductPaginationResponse;
import com.habbatul.challange4.model.responses.ProductResponse;
import com.habbatul.challange4.repository.ProductRepository;
import com.habbatul.challange4.repository.MerchantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

//bikin static biar langsung
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
class ProductServiceTest {

    @Spy
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        Mockito.reset(merchantRepository);
        Mockito.reset(productRepository);
    }

    @Test
    void testAddProduct() {
        CreateProductRequest request = CreateProductRequest.builder()
                .productName("TestProduct")
                .price(100.0)
                .merchantName("TestMerchant")
                .build();
        when(productRepository.existsByProductName("TestProduct")).thenReturn(false);

        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .build();
        when(merchantRepository.findByMerchantName("TestMerchant")).thenReturn(Optional.of(merchant));

        ProductResponse response = productService.addProduct(request);

        assertNotNull(response);
        assertEquals("TestProduct", response.getProductName());
        assertEquals(100.0, response.getPrice());
        assertEquals("TestMerchant", response.getMerchantName());

        verify(productRepository, times(1)).existsByProductName("TestProduct");
        verify(merchantRepository, times(1)).findByMerchantName("TestMerchant");
        verify(productService,times(1)).addProduct(request);
    }

    @Test
    void testAddProductWhenProductExists() {

        CreateProductRequest request = CreateProductRequest.builder()
                .productName("TestProduct")
                .price(100.0)
                .merchantName("TestMerchant")
                .build();

        when(merchantRepository.findByMerchantName("TestMerchant")).thenReturn(Optional.of(
                Merchant.builder().merchantName("TestMerchant").build()));
        when(productRepository.existsByProductName("TestProduct")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> productService.addProduct(request));

        verify(merchantRepository, times(1)).findByMerchantName("TestMerchant");
        verify(productRepository, times(1)).existsByProductName("TestProduct");
        verify(productService, times(1)).addProduct(request);
    }

    @Test
    void testAddProductWhenMerchantNotFound() {
        CreateProductRequest request = CreateProductRequest.builder()
                .productName("TestProduct")
                .price(100.0)
                .merchantName("TestMerchant")
                .build();

        when(merchantRepository.findByMerchantName("TestMerchant")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.addProduct(request));

        verify(merchantRepository, times(1)).findByMerchantName("TestMerchant");
        verify(productService, times(1)).addProduct(request);
    }

    @Test
    void testUpdateProduct() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .productName("UpdatedProduct")
                .price(150.0)
                .merchantName("UpdatedMerchant")
                .build();

        Product existingProduct = Product.builder()
                .productCode("123")
                .productName("TestProduct")
                .price(100.0)
                .merchant(Merchant.builder().merchantName("TestMerchant").build())
                .build();

        when(productRepository.findById("123")).thenReturn(Optional.of(existingProduct));

        Merchant updatedMerchant = Merchant.builder()
                .merchantName("UpdatedMerchant")
                .build();

        when(merchantRepository.findByMerchantName("UpdatedMerchant")).thenReturn(Optional.of(updatedMerchant));

        Product updatedProduct = Product.builder()
                .productCode("123")
                .productName("UpdatedProduct")
                .price(150.0)
                .merchant(updatedMerchant)
                .build();

        when(productRepository.save(any())).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(request, "123");

        assertNotNull(response);
        assertEquals("123", response.getProductCode());
        assertEquals("UpdatedProduct", response.getProductName());
        assertEquals(150.0, response.getPrice());
        assertEquals("UpdatedMerchant", response.getMerchantName());

        verify(productRepository, times(1)).findById("123");
        verify(productRepository).save(any());
        verify(productRepository).findById("123");
        verify(merchantRepository).findByMerchantName("UpdatedMerchant");
    }

    @Test
    void testUpdateProductWhenProductNotFound() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .productName("UpdatedProduct")
                .price(150.0)
                .merchantName("UpdatedMerchant")
                .build();

        when(productRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.updateProduct(request, "123"));

        verify(productRepository, times(1)).findById("123");
        verify(productService, times(1)).updateProduct(request, "123");
    }

    @Test
    void testUpdateProductWhenMerchantNotFound() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .productName("UpdatedProduct")
                .price(150.0)
                .merchantName("UpdatedMerchant")
                .build();

        when(merchantRepository.findByMerchantName("UpdatedMerchant")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.updateProduct(request, "123"));

        verify(productRepository, times(1)).findById("123");
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById("123")).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct("123"));

        verify(productRepository, times(1)).existsById("123");
        verify(productRepository, times(1)).deleteById("123");
    }

    @Test
    void testDeleteProductWhenProductNotFound() {
        when(productRepository.existsById("123")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> productService.deleteProduct("123"));

        verify(productRepository, times(1)).existsById("123");
    }

    @Test
    void testShowProduct() {
        Product product = Product.builder()
                .productCode("123")
                .productName("TestProduct")
                .price(100.0)
                .merchant(Merchant.builder().merchantName("TestMerchant").build())
                .build();

        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));

        when(productRepository.findAllProductsJoinMerchant(any())).thenReturn(Optional.of(productPage));

        ProductPaginationResponse response = productService.showProduct(1);

        assertNotNull(response);
        assertEquals(1, response.getProductCurrentPage());
        assertEquals(1, response.getProductTotalPage());

        ProductResponse productResponse = response.getProductsResponse().get(0);
        assertEquals("123", productResponse.getProductCode());
        assertEquals("TestProduct", productResponse.getProductName());
        assertEquals(100.0, productResponse.getPrice());
        assertEquals("TestMerchant", productResponse.getMerchantName());

        verify(productRepository, times(1)).findAllProductsJoinMerchant(any());
    }

    @Test
    void testShowProductWhenNoDataFound() {
        when(productRepository.findAllProductsJoinMerchant(any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.showProduct(1));

        verify(productRepository, times(1)).findAllProductsJoinMerchant(any());
    }

    @Test
    void testShowProductWhenPageContentBlank() {
        Page<Product> productPage = new PageImpl<>(Collections.emptyList());

        when(productRepository.findAllProductsJoinMerchant(any())).thenReturn(Optional.of(productPage));

        assertThrows(ResponseStatusException.class, () -> productService.showProduct(5));

        verify(productRepository, times(1)).findAllProductsJoinMerchant(any());
    }
}

