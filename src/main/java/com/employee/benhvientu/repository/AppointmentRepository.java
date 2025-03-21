package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByUser(User user);
    Appointment findByAppointmentIdAndUserUsername(int appointmentId, String username);
}