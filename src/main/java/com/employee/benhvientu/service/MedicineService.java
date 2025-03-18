package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.MedicineDTO;
import com.employee.benhvientu.entity.Medicine;
import com.employee.benhvientu.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineService {
    @Autowired
    private MedicineRepository medicineRepository;

    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MedicineDTO convertToDTO(Medicine medicine) {
        MedicineDTO dto = new MedicineDTO();
        dto.setMedicineId(medicine.getMedicineId());
        dto.setName(medicine.getName());
        dto.setDescription(medicine.getDescription());
        dto.setUnitPrice(medicine.getUnitPrice());
        dto.setCreatedAt(medicine.getCreatedAt());
        return dto;
    }
}