package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.MedicalRecordDTO;
import com.employee.benhvientu.entity.MedicalRecord;
import com.employee.benhvientu.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping("/create")
    public MedicalRecordDTO createMedicalRecord(@RequestBody MedicalRecordDTO request, Authentication authentication) {
        String doctorUsername = authentication.getName();
        return medicalRecordService.createMedicalRecord(doctorUsername, request);
    }

    @GetMapping("/list")
    public List<MedicalRecordDTO> getMedicalRecords(Authentication authentication) {
        String username = authentication.getName();
        return medicalRecordService.getMedicalRecords(username);
    }
    @GetMapping("/{id}")
    public MedicalRecordDTO getMedicalRecordById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return medicalRecordService.getMedicalRecordById(id, username);
    }

}