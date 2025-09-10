package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByTransactionId(String payosOrderCode);
}
