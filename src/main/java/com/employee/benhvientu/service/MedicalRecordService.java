package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.DoctorInfoDTO;
import com.employee.benhvientu.dto.MedicalRecordDTO;
import com.employee.benhvientu.dto.PatientInfoDTO;
import com.employee.benhvientu.entity.MedicalRecord;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.MedicalRecordRepository;
import com.employee.benhvientu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private UserRepository userRepository;

    public MedicalRecordDTO createMedicalRecord(String doctorUsername, Long patientId, MedicalRecordDTO request) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        // Kiểm tra quyền của bác sĩ
        if (!doctor.getRoleCode().equals("MGR")) {
            throw new RuntimeException("Chỉ có bác sĩ mới có thể tạo hồ sơ bệnh án");
        }
        if (!doctor.getDepartmentId().equals(patient.getDepartmentId()) || !patient.getRoleCode().equals("EMP")) {
            throw new RuntimeException("Bác sĩ chỉ có thể tạo hồ sơ cho bệnh nhân trong cùng một khoa");
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDoctor(doctor);
        medicalRecord.setPatient(patient);
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setMedicalHistory(request.getMedicalHistory());
        medicalRecord.setAllergies(request.getAllergies());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTestResults(request.getTestResults());
        medicalRecord.setPrescription(request.getPrescription());
        medicalRecord.setNotes(request.getNotes());
        medicalRecord = medicalRecordRepository.save(medicalRecord);

        return convertToDTO(medicalRecord);
    }
    public MedicalRecordDTO getMedicalRecordById(Long id, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh án!"));

        if ("ADM".equals(currentUser.getRoleCode())) {
            return convertToDTO(medicalRecord);
        }

        if ("MGR".equals(currentUser.getRoleCode())) {
            if (!medicalRecord.getPatient().getRoleCode().equals("EMP") ||
                    !medicalRecord.getPatient().getDepartmentId().equals(currentUser.getDepartmentId())) {
                throw new RuntimeException("Bác sĩ chỉ xem được hồ sơ khám của bệnh nhân cùng department");
            }
            return convertToDTO(medicalRecord);
        }

        if ("EMP".equals(currentUser.getRoleCode())) {
            if (!medicalRecord.getPatient().getUserId().equals(currentUser.getUserId())) {
                throw new RuntimeException("Bệnh nhân chỉ được xem hồ sơ khám của bản thân");
            }
            return convertToDTO(medicalRecord);
        }

        throw new RuntimeException("Truy cập trái phép!");
    }


    public List<MedicalRecordDTO> getMedicalRecords(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        List<MedicalRecord> records;

        if (user.getRoleCode().equals("ADM")) {
            // Admin có thể xem tất cả hồ sơ bệnh án
            records = medicalRecordRepository.findAll();
        } else if (user.getRoleCode().equals("MGR")) {
            // Bác sĩ chỉ có thể xem hồ sơ bệnh án của bệnh nhân (EMP) trong cùng phòng ban
            records = medicalRecordRepository.findByDoctor_DepartmentIdAndPatient_RoleCode(user.getDepartmentId(), "EMP");
        } else if (user.getRoleCode().equals("EMP")) {
            // Bệnh nhân chỉ có thể xem hồ sơ bệnh án của chính họ
            records = medicalRecordRepository.findByPatient_UserId(user.getUserId());
        } else {
            throw new RuntimeException("Truy cập trái phép!");
        }

        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setRecordId(record.getRecordId());
        dto.setPatientName(record.getPatient().getName());
        dto.setGender(record.getPatient().getGender().name());
        dto.setAddress(record.getPatient().getAddress());
        dto.setInsuranceNumber(record.getPatient().getInsuranceNumber());
        dto.setSymptoms(record.getSymptoms());
        dto.setMedicalHistory(record.getMedicalHistory());
        dto.setAllergies(record.getAllergies());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setTestResults(record.getTestResults());
        dto.setPrescription(record.getPrescription());
        dto.setNotes(record.getNotes());

        return dto;
    }

    private PatientInfoDTO convertToPatientInfo(User patient) {
        PatientInfoDTO dto = new PatientInfoDTO();
        dto.setUserId(patient.getUserId());
        dto.setName(patient.getName());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());
        dto.setGender(patient.getGender().name()); // Chuyển Enum thành String
        dto.setRoleCode(patient.getRoleCode());
        dto.setDepartmentId(patient.getDepartmentId());
        dto.setCccd(patient.getCccd());
        dto.setInsuranceNumber(patient.getInsuranceNumber());
        dto.setAddress(patient.getAddress());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }

    private DoctorInfoDTO convertToDoctorInfo(User doctor) {
        DoctorInfoDTO dto = new DoctorInfoDTO();
        dto.setName(doctor.getName());
        dto.setPhone(doctor.getPhone());
        return dto;
    }
}
