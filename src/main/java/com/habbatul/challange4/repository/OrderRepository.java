package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.Order;
import com.habbatul.challange4.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o INNER JOIN FETCH o.user u INNER JOIN FETCH o.orderDetails od JOIN FETCH od.product pr WHERE u.username = :username")
    List<Order> findOrdersByUserUsername(@Param("username") String username);

    @Query("SELECT o FROM Order o INNER JOIN FETCH o.user u INNER JOIN FETCH o.orderDetails od JOIN FETCH od.product pr")
    List<Order> findOrdersAll();

    //pagable tidak bisa dikasih join fetch
    @Query("SELECT o FROM Order o LEFT JOIN o.user u INNER JOIN o.orderDetails od WHERE u.username = :username AND o.completed = 'INCOMPLETE' ORDER BY o.orderId DESC")
    Page<Order> findOneOrdersByUserUsername(@Param("username") String username, Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.completed = 'COMPLETE' WHERE o.orderId = :orderId")
    void updateCompletedStatus(@Param("orderId") Long orderId);

    void deleteAllByCompleted(OrderStatus orderStatus);
}
