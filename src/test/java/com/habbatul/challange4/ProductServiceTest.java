package com.habbatul.challange4;


import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.ProductPaginationResponse;
import com.habbatul.challange4.model.ProductResponse;
import com.habbatul.challange4.repository.MerchantRepository;
import com.habbatul.challange4.repository.ProductRepository;
import com.habbatul.challange4.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @BeforeEach
    @AfterEach
    void cleanDB() {
        productRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    @Test
    void testAddProduct() {
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .build();
        merchantRepository.save(merchant);

        Product product = Product.builder()
                .productName("TestProduct")
                .price(100.0)
                .merchant(merchant)
                .build();

        //gunakan service
        ProductResponse response = productService.addProduct(product, merchant);

        //cek response service
        assertEquals("TestProduct", response.getProductName());
        assertEquals(100.0, response.getPrice());
        assertEquals("TestMerchant", response.getMerchantName());

        //cek produk pada database apakah tersimpan
        Product savedProduct = productRepository.findById(product.getProductCode()).orElse(null);
        assertEquals("TestProduct", savedProduct.getProductName());
        assertEquals(100.0, savedProduct.getPrice());
        assertEquals("TestMerchant", savedProduct.getMerchant().getMerchantName());
    }


    @Test
    void testAddProductSameName() {
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .build();
        merchantRepository.save(merchant);

        Product product = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct")
                .price(100.0)
                .merchant(merchant)
                .build();

        productRepository.save(product);

        //cek bila menambahkan nama produk sama
        assertThrows(CustomException.class, () -> productService.addProduct(product, merchant));
    }


    @Test
    void testUpdateProduct() {
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .build();
        merchantRepository.save(merchant);

        Product product = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct")
                .price(100.0)
                .merchant(merchant)
                .build();
        productRepository.save(product);

        //update harganya
        product.setPrice(500.0);

        //gunakan service
        ProductResponse response = productService.updateProduct(product);

        assertEquals(500.0, response.getPrice());

        //cek bahwa harga berhasil diupdate di db
        Product updatedProduct = productRepository.findById(product.getProductCode()).orElse(null);
        assertEquals(500.0, updatedProduct.getPrice());
    }


    @Test
    void testUpdateProductNotFound() {
        Product product = Product.builder()
                .productCode("error") //code yang tidak ada
                .build();

        //cek bila mengupdate produk yang tidak ada
        assertThrows(CustomException.class, () -> productService.updateProduct(product));
    }


    @Test
    void testDeleteProduct() {
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .build();
        merchantRepository.save(merchant);

        Product product = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("TestProduct")
                .price(100.0)
                .merchant(merchant)
                .build();
        productRepository.save(product);

        productService.deleteProduct(product);

        //cek bahwa data produk berhasil terhapus
        Product deletedProduct = productRepository.findById(product.getProductCode()).orElse(null);
        assertEquals(null, deletedProduct);
    }


    @Test
    void testDeleteProductNotFound() {
        Product product = Product.builder()
                .productCode("error") //code yang tidak ada
                .build();

        assertThrows(CustomException.class, () -> productService.deleteProduct(product));
    }


    @Test
    void testShowProduct() {
        // Create a merchant in the database
        Merchant merchant = Merchant.builder()
                .merchantName("TestMerchant")
                .build();
        merchantRepository.save(merchant);

        // Create a few products in the database
        Product product1 = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("Product1")
                .price(100.0)
                .merchant(merchant)
                .build();
        productRepository.save(product1);

        Product product2 = Product.builder()
                .addedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .productName("Product2")
                .price(200.0)
                .merchant(merchant)
                .build();
        productRepository.save(product2);

        //disini pageable nya itu Desc berdasarkan productid


        //test response product untuk halaman 1
        List<ProductPaginationResponse> response = productService.showProduct(1);

        assertEquals(1, response.size());
        response.forEach(pagination -> {
            //cek product pada page 1
            List<ProductResponse> responseProduct = pagination.getProductsResponse();
            responseProduct.forEach(products -> {
                assertEquals("Product1", products.getProductName());
                assertEquals(100.0, products.getPrice());
                assertEquals("TestMerchant", products.getMerchantName());

            });
            //cek total page
            assertEquals(2, pagination.getProductTotalPage());
            //cek halaman saat ini
            assertEquals(1, pagination.getProductCurrentPage());
        });


        //test response product untuk halaman 1
        List<ProductPaginationResponse> response2 = productService.showProduct(2);
        assertEquals(1, response.size());
        response2.forEach(pagination -> {
            //cek product pada page 1
            List<ProductResponse> responseProduct = pagination.getProductsResponse();
            responseProduct.forEach(products -> {
                assertEquals("Product2", products.getProductName());
                assertEquals(200.0, products.getPrice());
                assertEquals("TestMerchant", products.getMerchantName());
            });
            //cek total page
            assertEquals(2, pagination.getProductTotalPage());
            //cek halaman saat ini
            assertEquals(2, pagination.getProductCurrentPage());
        });

    }

}
