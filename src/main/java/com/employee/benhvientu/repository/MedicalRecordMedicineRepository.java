package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.MedicalRecordMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordMedicineRepository extends JpaRepository<MedicalRecordMedicine, Integer> {
}
