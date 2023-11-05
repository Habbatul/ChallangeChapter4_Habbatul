package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p INNER JOIN p.merchant m ORDER BY p.addedTime ASC")
    Optional<Page<Product>> findAllProductsJoinMerchant(Pageable pageable);

    boolean existsByProductName(String productName);

    Optional<Product> findByProductName(String productName);

}
