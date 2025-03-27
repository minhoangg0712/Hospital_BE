package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.Department;
import com.employee.benhvientu.entity.User;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByUser(User user);
    Appointment findByAppointmentIdAndUserUsername(int appointmentId, String username);
    List<Appointment> findByDepartment(Department department);
    boolean existsByUserAndAppointmentDate(User user, Date appointmentDate);
}