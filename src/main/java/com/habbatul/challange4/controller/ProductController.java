package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.requests.CreateProductRequest;
import com.habbatul.challange4.model.requests.UpdateProductRequest;
import com.habbatul.challange4.model.responses.ProductPaginationResponse;
import com.habbatul.challange4.model.responses.ProductResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Menambahkan produk baru")
    @PostMapping(value = "/product",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ProductResponse>> addProduct(
            @RequestBody CreateProductRequest createProductRequest) {

        ProductResponse response = productService.addProduct(createProductRequest);
        WebResponse<ProductResponse> productResponseWebResponse = WebResponse.<ProductResponse>builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseWebResponse);
    }

    @Operation(summary = "Mengupdate detail produk")
    @PutMapping(value = "/product/{productCode}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ProductResponse>> updateProduct(
            @PathVariable String productCode,
            @RequestBody UpdateProductRequest productRequest) {

        ProductResponse response = productService.updateProduct(productRequest, productCode);
        WebResponse<ProductResponse> productResponseWebResponse = WebResponse.<ProductResponse>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(productResponseWebResponse);
    }

    @Operation(summary = "Menghapus produk")
    @DeleteMapping(value = "/product/{productCode}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteProduct(@PathVariable String productCode) {

        productService.deleteProduct(productCode);
        return ResponseEntity.ok().body(WebResponse.<String>builder().data("OK").build());
    }


    @Operation(summary = "Menampilkan produk yang tersedia")
    @GetMapping(value = "/product",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ProductPaginationResponse>> showProduct(
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        ProductPaginationResponse response = productService.showProduct(page);
        WebResponse<ProductPaginationResponse> productResponseWebResponse = WebResponse.<ProductPaginationResponse>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(productResponseWebResponse);
    }
}
