package com.employee.benhvientu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MEDICAL_RECORD_MEDICINES")
@Getter
@Setter
public class MedicalRecordMedicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordMedicineId;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity;

    public Integer getRecordMedicineId() {
        return recordMedicineId;
    }

    public void setRecordMedicineId(Integer recordMedicineId) {
        this.recordMedicineId = recordMedicineId;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
