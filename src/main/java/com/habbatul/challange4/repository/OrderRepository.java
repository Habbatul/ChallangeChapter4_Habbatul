package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o INNER JOIN o.user u INNER JOIN o.orderDetails od WHERE u.userId = :userId")
    List<Order> findOrdersByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o INNER JOIN o.user u INNER JOIN o.orderDetails od")
    List<Order> findOrdersAll();
}
