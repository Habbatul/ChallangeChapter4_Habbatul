package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.entity.Product;
import com.habbatul.challange4.model.ProductPaginationResponse;
import com.habbatul.challange4.model.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(Product product, Merchant merchant);
    ProductResponse updateProduct(Product product);
    void deleteProduct(Product product);

    List<ProductPaginationResponse> showProduct(Integer page);
}
