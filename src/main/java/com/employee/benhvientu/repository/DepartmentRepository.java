package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    // Có thể thêm các method tùy chỉnh nếu cần
}