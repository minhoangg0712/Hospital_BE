package com.employee.benhvientu.controller;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.Department;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('AST')")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    // Lấy danh sách tất cả lịch hẹn chờ xử lý
    @GetMapping("/pending-appointments")
    public ResponseEntity<List<Appointment>> getPendingAppointments() {
        return ResponseEntity.ok(assistantService.getPendingAppointments());
    }

    // Lấy danh sách lịch hẹn chờ xử lý theo khoa
    @GetMapping("/pending-appointments/{departmentId}")
    public ResponseEntity<List<Appointment>> getPendingAppointmentsByDepartment(@PathVariable Integer departmentId) {
        return ResponseEntity.ok(assistantService.getPendingAppointmentsByDepartment(departmentId));
    }

    // Lấy danh sách bác sĩ trong khoa
    @GetMapping("/doctors/{departmentId}")
    public ResponseEntity<List<User>> getDoctorsByDepartment(@PathVariable Integer departmentId) {
        return ResponseEntity.ok(assistantService.getDoctorsByDepartment(departmentId));
    }

    // Lấy lịch làm việc của bác sĩ
    @GetMapping("/doctor-schedule/{doctorId}")
    public ResponseEntity<List<Appointment>> getDoctorSchedule(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(assistantService.getDoctorSchedule(doctorId));
    }

    // Xác nhận và gán lịch hẹn cho bác sĩ
    @PutMapping("/confirm-appointment/{appointmentId}")
    public ResponseEntity<Appointment> confirmAppointment(
            @PathVariable Integer appointmentId, 
            @RequestParam Integer doctorId, 
            Authentication authentication) {
        String assistantUsername = authentication.getName();
        return ResponseEntity.ok(assistantService.confirmAppointment(appointmentId, doctorId, assistantUsername));
    }

    // Lấy tất cả lịch hẹn
    @GetMapping("/all-appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(assistantService.getAllAppointments());
    }

    // Lấy tất cả lịch hẹn trong khoa cụ thể
    @GetMapping("/appointments/{departmentId}")
    public ResponseEntity<List<Appointment>> getAllAppointmentsInDepartment(@PathVariable Integer departmentId) {
        return ResponseEntity.ok(assistantService.getAllAppointmentsInDepartment(departmentId));
    }

    // Lấy danh sách tất cả khoa
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(assistantService.getAllDepartments());
    }
}
