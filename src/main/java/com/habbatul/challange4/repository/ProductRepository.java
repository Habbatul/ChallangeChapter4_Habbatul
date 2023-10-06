package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p INNER JOIN p.merchant m ORDER BY p.addedTime ASC")
    Page<Product> findAllProductsJoinMerchant(Pageable pageable);


    boolean existsByProductName(String productName);

}
