package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    // Tìm hồ sơ bệnh án của bệnh nhân cụ thể
    List<MedicalRecord> findByPatient_UserId(Long userId);

    // Tìm hồ sơ bệnh án của bệnh nhân (EMP) trong cùng phòng ban của bác sĩ (MGR)
    List<MedicalRecord> findByDoctor_DepartmentIdAndPatient_RoleCode(Integer departmentId, String roleCode);
}
