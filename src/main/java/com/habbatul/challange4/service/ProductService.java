package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.model.requests.CreateProductRequest;
import com.habbatul.challange4.model.requests.UpdateProductRequest;
import com.habbatul.challange4.model.responses.ProductPaginationResponse;
import com.habbatul.challange4.model.responses.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(CreateProductRequest createProductRequest);
    ProductResponse updateProduct(UpdateProductRequest updateProductRequest, String productCode);
    void deleteProduct(String productCode);

    ProductPaginationResponse showProduct(Integer page);
}
