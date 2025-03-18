package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    // Tìm thuốc theo tên
    Optional<Medicine> findByName(String name);

    // Tìm thuốc theo tên chứa keyword
    List<Medicine> findByNameContainingIgnoreCase(String keyword);

    // Tìm thuốc theo khoảng giá
    List<Medicine> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}