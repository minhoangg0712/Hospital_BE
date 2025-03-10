package com.employee.benhvientu.repository;


import com.employee.benhvientu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByCccd(String cccd);
    Optional<User> findByInsuranceNumber(String insuranceNumber);
    List<User> findByRoleCode(String roleCode);
    List<User> findByRoleCodeAndDepartmentId(String roleCode, Integer departmentId);
    List<User> findByDepartmentId(Integer departmentId); // Thêm phương thức này

}


