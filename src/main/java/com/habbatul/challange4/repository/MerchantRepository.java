package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.Merchant;
import com.habbatul.challange4.enums.MerchantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String> {
    boolean existsByMerchantName(String merchantName);

    @Query("SELECT p FROM Merchant p WHERE p.open = :codeMerchant")
    List<Merchant> findMerchantByStatus(@Param("codeMerchant") MerchantStatus codeMerchant);
}
