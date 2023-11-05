package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.model.requests.CreateProductRequest;
import com.habbatul.challange4.model.requests.UpdateProductRequest;
import com.habbatul.challange4.model.responses.ProductPaginationResponse;
import com.habbatul.challange4.model.responses.ProductResponse;
import com.habbatul.challange4.repository.MerchantRepository;
import com.habbatul.challange4.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;


    @Autowired
    MerchantRepository merchantRepository;

    @Override
    public ProductResponse addProduct(CreateProductRequest createProductRequest) {
        Product product = Product.builder()
                .productName(createProductRequest.getProductName())
                .price(createProductRequest.getPrice())
                .build();

        Merchant merchant = new Merchant();
        merchant.setMerchantName(createProductRequest.getMerchantName());

        log.debug("Menjalankan service addProduct");

        //jadi set merchant pakek parameter merchant kalo ga ada dia ngethrow, jadi santuy
        Merchant merchantFound = merchantRepository.findByMerchantName(merchant.getMerchantName())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Merchant tidak ditemukan"));
        product.setMerchant(merchantFound);

        if (!productRepository.existsByProductName(product.getProductName())) {
            //kondisional sementara
            //tambah waktunya
            if (product.getAddedTime() == null)
                product.setAddedTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            //tambah merchant dari parameter

            productRepository.save(product);
            log.info("Berhasil menyimpan product");
            return toProductResponse(product, merchantFound);
        } else {
            log.error("Product sudah ada");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product sudah ada");
        }
    }


    private ProductResponse toProductResponse(Product product, Merchant merchant) {
        log.debug("Memberikan response product");
        return ProductResponse.builder()
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .price(product.getPrice())
                .merchantName(merchant != null ? merchant.getMerchantName() : null)
                .build();
    }

    @Override
    public ProductResponse updateProduct(UpdateProductRequest updateReq, String productCode) {
        Product product = Product.builder()
                .productCode(productCode)
                .productName(updateReq.getProductName())
                .price(updateReq.getPrice())
                .build();
        Merchant merchant = new Merchant();
        merchant.setMerchantName(updateReq.getMerchantName());


        Optional<Product> productByID = productRepository.findById(product.getProductCode());
        if (productByID.isPresent()) {
            Product oldProduct = productByID.get();
            oldProduct.setProductName(product.getProductName() != null ? product.getProductName() : oldProduct.getProductName());
            oldProduct.setPrice(product.getPrice() != null ? product.getPrice() : oldProduct.getPrice());

            //logic sementara
            if(merchant.getMerchantName() != null){
                Merchant merchantByName = merchantRepository.findByMerchantName(merchant.getMerchantName())
                        .orElse(null);
                oldProduct.setMerchant(merchantByName);
            }

            productRepository.save(oldProduct);
            log.info("Berhasil mengubah product {}", product.getProductName());
            return toProductResponse(oldProduct, oldProduct.getMerchant());
        } else {
            log.error("Produk tidak ditemukan.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product tidak ditemukan");
        }

    }

    @Override
    public void deleteProduct(String productCode) {
        log.debug("Menjalankan service deleteProduct, kode");
        if (productRepository.existsById(productCode)){
            productRepository.deleteById(productCode);
            log.info("Berhasil melakukan delete");
        } else {
            log.error("Product tidak ditemukan");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product tidak ditemukan");
        }
    }

    private ProductPaginationResponse toProductPaginationResponse(Page<Product> productPage) {
        log.debug("Memberikan response pagination");

        List<Product> productResponses = productPage.getContent();

        List<ProductResponse> productResponseList = productResponses.stream()
                .map(product -> toProductResponse(product, product.getMerchant()))
                .collect(Collectors.toList());

        return ProductPaginationResponse.builder()
                .productsResponse(productResponseList)
                .productCurrentPage(productPage.getNumber() + 1)
                .productTotalPage(productPage.getTotalPages())
                .build();
    }

    @Override
    public ProductPaginationResponse showProduct(Integer page) {
        log.debug("Menjalankan service showProduct");

        page -= 1; //halaman asli dari index 0
        //sementara size nya 1
        Pageable halaman = PageRequest.of(page, 3);

        Page<Product> productPage = productRepository.findAllProductsJoinMerchant(halaman)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data tidak ditemukan"));

        if (productPage.getContent().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Halaman tidak memiliki item");
        }

        log.info("Berhasil mendapatkan item Product");

        return toProductPaginationResponse(productPage);
    }
}
