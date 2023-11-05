package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
