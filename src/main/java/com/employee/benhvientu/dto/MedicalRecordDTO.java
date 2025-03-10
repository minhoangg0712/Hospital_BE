package com.employee.benhvientu.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicalRecordDTO {
    private long patientId;
    private String reason;
    private PatientInfoDTO patient;
    private DoctorInfoDTO doctor;
    private String medicalHistory;
    private String diagnosis;
    private String testResults;
    private String finalDiagnosis;
    private List<MedicinePrescriptionDTO> medicines;

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }

    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }

    public void setFinalDiagnosis(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
    }

    public List<MedicinePrescriptionDTO> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<MedicinePrescriptionDTO> medicines) {
        this.medicines = medicines;
    }

    public PatientInfoDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientInfoDTO patient) {
        this.patient = patient;
    }

    public DoctorInfoDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorInfoDTO doctor) {
        this.doctor = doctor;
    }
}
