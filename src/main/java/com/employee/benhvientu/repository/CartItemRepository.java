package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.CartItem;
import com.employee.benhvientu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Lấy tất cả item trong giỏ hàng của user
    List<CartItem> findByUser(User user);

    // Tìm item cụ thể trong giỏ hàng của user
    Optional<CartItem> findByUserAndMedicine_MedicineId(User user, Long medicineId);

    // Xóa tất cả item trong giỏ hàng của user
    void deleteByUser(User user);
}