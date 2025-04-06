package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.MedicalRecordDTO;
import com.employee.benhvientu.dto.RelativeMedicalRecordDTO;
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

    @PostMapping("/create/{patientId}")
    public MedicalRecordDTO createMedicalRecord(
            @PathVariable Long patientId,
            @RequestBody MedicalRecordDTO request,
            Authentication authentication) {
        String doctorUsername = authentication.getName();
        return medicalRecordService.createMedicalRecord(doctorUsername, patientId, request);
    }

    @PostMapping("/create-relative/{patientId}/{appointmentId}")
    public RelativeMedicalRecordDTO createRelativeMedicalRecord(
            @PathVariable Long patientId,
            @PathVariable Integer appointmentId,
            @RequestBody MedicalRecordDTO request,
            Authentication authentication) {
        String doctorUsername = authentication.getName();
        return medicalRecordService.createRelativeMedicalRecord(doctorUsername, patientId, appointmentId, request);
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

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordDTO> getMedicalRecordsByPatientId(
            @PathVariable Long patientId,
            Authentication authentication) {
        String username = authentication.getName();
        return medicalRecordService.getMedicalRecordsByPatientId(patientId, username);
    }

}