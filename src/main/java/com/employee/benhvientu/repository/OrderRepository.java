package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findById(Integer orderId);

}
