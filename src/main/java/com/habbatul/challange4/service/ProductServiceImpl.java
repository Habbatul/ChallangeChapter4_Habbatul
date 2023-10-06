package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.exception.CustomException;
import com.habbatul.challange4.model.ProductPaginationResponse;
import com.habbatul.challange4.model.ProductResponse;
import com.habbatul.challange4.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public ProductResponse addProduct(Product product, Merchant merchant) {
        log.debug("Menjalankan service addProduct");

        if (!productRepository.existsByProductName(product.getProductName())) {

            if (product.getAddedTime() == null)
                product.setAddedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));

            productRepository.save(product);
            log.info("Berhasil menyimpan product");
            return toProductResponse(product, merchant);
        } else {
            log.error("Product sudah ada");
            throw new CustomException("Product is exist!!");
        }
    }


    private ProductResponse toProductResponse(Product product, Merchant merchant) {
        log.debug("Memberikan response product");
        return ProductResponse.builder()
                .productName(product.getProductName())
                .price(product.getPrice())
                .merchantName(merchant != null ? merchant.getMerchantName() : null)
                .build();
    }

    @Override
    public ProductResponse updateProduct(Product product) {

        Optional<Product> productByID = productRepository.findById(product.getProductCode());
        if (productByID.isPresent()) {
            Product oldProduct = productByID.get();
            oldProduct.setProductName(product.getProductName() != null ? product.getProductName() : oldProduct.getProductName());
            oldProduct.setPrice(product.getPrice() != null ? product.getPrice() : oldProduct.getPrice());
            oldProduct.setMerchant(product.getMerchant() != null ? product.getMerchant() : oldProduct.getMerchant());
            productRepository.save(oldProduct);
            log.info("Berhasil mengubah product {}", product.getProductName());
            return toProductResponse(oldProduct, oldProduct.getMerchant());
        } else {
            log.error("Produk tidak ditemukan");
            throw new CustomException("Product Not found");
        }

    }

    @Override
    public void deleteProduct(Product product) {
        log.debug("Menjalankan service deleteProduct");
        if (productRepository.existsById(product.getProductCode())) {
            productRepository.deleteById(product.getProductCode());
            log.info("Berhasil melakukan delete {}", product.getProductName());
        } else {
            log.error("Product tidak ditemukan");
            throw new CustomException("Product Not found");
        }
    }

    private List<ProductPaginationResponse> toProductPaginationResponse(Page<Product> productPage) {
        log.debug("Memberikan response pagination");

        List<Product> productResponses = productPage.getContent();
        List<ProductPaginationResponse> paginationResponses = new ArrayList<>();

        for (Product product : productResponses) {
            paginationResponses.add(ProductPaginationResponse.builder()
                    .productsResponse(Collections.singletonList(toProductResponse(product, product.getMerchant())))
                    .productCurrentPage(productPage.getNumber() + 1)
                    .productTotalPage(productPage.getTotalPages())
                    .build());
        }

        return paginationResponses;
    }

    @Override
    public List<ProductPaginationResponse> showProduct(Integer page) {
        log.debug("Menjalankan service showProduct");

        page -= 1; //halaman asli dari index 0
        //sementara size nya 1
        Pageable halaman = PageRequest.of(page, 1);

        Page<Product> productPage = productRepository.findAllProductsJoinMerchant(halaman);

        log.info("Berhasil mendapatkan item Product");

        return toProductPaginationResponse(productPage);
    }
}
