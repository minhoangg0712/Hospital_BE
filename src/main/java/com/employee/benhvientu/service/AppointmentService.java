package com.employee.benhvientu.service;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.Department;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.AppointmentRepository;
import com.employee.benhvientu.repository.DepartmentRepository;
import com.employee.benhvientu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Appointment createAppointment(Appointment appointment, String username, boolean forSelf) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Department department = departmentRepository.findById(appointment.getDepartment().getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        appointment.setUser(user);
        appointment.setDepartment(department);

        if (!forSelf) {
            if (appointment.getRelativeName() == null || appointment.getRelativeIdCard() == null) {
                throw new IllegalArgumentException("Relative name and ID card must be provided for relative appointments.");
            }
        }

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> listAppointments(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Appointment> appointments = appointmentRepository.findByUser(user);
        appointments.forEach(appointment -> {
            Department department = departmentRepository.findById(appointment.getDepartment().getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            appointment.getDepartment().setDepartmentName(department.getDepartmentName());
        });
        return appointments;
    }

    public List<Appointment> listAppointmentsByDepartment(String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        Department department = departmentRepository.findById(doctor.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        return appointmentRepository.findByDepartment(department);
    }

    public Appointment updateAppointment(int id, Appointment appointment, String username) {
        Appointment existingAppointment = appointmentRepository.findByAppointmentIdAndUserUsername(id, username);
        if (existingAppointment != null) {
            existingAppointment.setAppointmentDate(appointment.getAppointmentDate());
            existingAppointment.setStatus(appointment.getStatus());
            existingAppointment.setDepartment(appointment.getDepartment());
            Department department = departmentRepository.findById(appointment.getDepartment().getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            existingAppointment.getDepartment().setDepartmentName(department.getDepartmentName());
            return appointmentRepository.save(existingAppointment);
        }
        throw new RuntimeException("Appointment not found or you do not have permission to update it.");
    }

    public void deleteAppointment(int id, String username) {
        Appointment existingAppointment = appointmentRepository.findByAppointmentIdAndUserUsername(id, username);
        if (existingAppointment != null) {
            appointmentRepository.delete(existingAppointment);
        } else {
            throw new RuntimeException("Appointment not found or you do not have permission to delete it.");
        }
    }
}