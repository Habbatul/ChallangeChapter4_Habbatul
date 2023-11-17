package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.model.requests.CreateProductRequest;
import com.habbatul.challange4.model.requests.UpdateProductRequest;
import com.habbatul.challange4.model.responses.ProductPaginationResponse;
import com.habbatul.challange4.model.responses.ProductResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    //async
    CompletableFuture<ProductResponse> addProductAsync(CreateProductRequest createProductRequest);

    ProductResponse addProduct(CreateProductRequest createProductRequest);
    ProductResponse updateProduct(UpdateProductRequest updateProductRequest, String productCode);
    void deleteProduct(String productCode);

    ProductPaginationResponse showProduct(Integer page);
}
