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

        return ResponseEntity.status(HttpStatus.CREATED).body(WebResponse.<ProductResponse>builder()
                .data(productService.addProduct(createProductRequest))
                .build());
    }

    @Operation(summary = "Mengupdate detail produk")
    @PutMapping(value = "/product/{productCode}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<ProductResponse>> updateProduct(
            @PathVariable String productCode,
            @RequestBody UpdateProductRequest productRequest) {

        return ResponseEntity.ok(WebResponse.<ProductResponse>builder()
                .data(productService.updateProduct(productRequest, productCode))
                .build());
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

        return ResponseEntity.ok(WebResponse.<ProductPaginationResponse>builder()
                .data(productService.showProduct(page))
                .build());
    }
}
