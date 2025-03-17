package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByPatientIdOrDoctorId(Long patientId, Long doctorId);
}
