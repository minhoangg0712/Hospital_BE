package com.employee.benhvientu.service;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.AppointmentRepository;
import com.employee.benhvientu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AssistantService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách tất cả lịch hẹn chờ xử lý (phụ tá có thể xem tất cả)
    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(Appointment.STATUS_PENDING);
    }

    // Lấy danh sách lịch hẹn chờ xử lý theo khoa cụ thể
    public List<Appointment> getPendingAppointmentsByDepartment(Integer departmentId) {
        return appointmentRepository.findByDepartment_DepartmentIdAndStatus(
                departmentId, 
                Appointment.STATUS_PENDING
        );
    }

    // Lấy danh sách bác sĩ trong khoa
    public List<User> getDoctorsByDepartment(Integer departmentId) {
        return userRepository.findByDepartmentIdAndRoleCode(departmentId, "MGR");
    }

    // Lấy lịch làm việc của bác sĩ
    public List<Appointment> getDoctorSchedule(Integer doctorId) {
        return appointmentRepository.findByDoctorUserIdAndStatus(
                doctorId, 
                Appointment.STATUS_CONFIRMED
        );
    }

    // Xác nhận và gán lịch hẹn cho bác sĩ
    public Appointment confirmAppointment(Integer appointmentId, Integer doctorId, String assistantUsername) {
        User assistant = userRepository.findByUsername(assistantUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ tá"));
        
        User doctor = userRepository.findById(doctorId.longValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
        
        // Kiểm tra xem bác sĩ có thuộc khoa của lịch hẹn không
        if (doctor.getDepartmentId() == null) {
            throw new RuntimeException("Bác sĩ chưa được gán vào khoa nào. Vui lòng liên hệ admin để gán khoa.");
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));
        
        // Kiểm tra xem bác sĩ có thuộc khoa của lịch hẹn không
        if (!doctor.getDepartmentId().equals(appointment.getDepartment().getDepartmentId())) {
            throw new RuntimeException("Bác sĩ không thuộc khoa của lịch hẹn này");
        }
        
        // Cập nhật lịch hẹn
        appointment.setDoctor(doctor);
        appointment.setStatus(Appointment.STATUS_CONFIRMED);
        appointment.setConfirmedBy(assistant);
        appointment.setConfirmedAt(new Date());
        
        return appointmentRepository.save(appointment);
    }

    // Lấy tất cả lịch hẹn
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Lấy tất cả lịch hẹn trong khoa cụ thể
    public List<Appointment> getAllAppointmentsInDepartment(Integer departmentId) {
        return appointmentRepository.findByDepartment_DepartmentId(departmentId);
    }

    // Lấy danh sách tất cả khoa
    public List<com.employee.benhvientu.entity.Department> getAllDepartments() {
        return appointmentRepository.findAll().stream()
                .map(Appointment::getDepartment)
                .distinct()
                .toList();
    }
}
