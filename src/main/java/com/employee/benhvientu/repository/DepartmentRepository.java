package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}