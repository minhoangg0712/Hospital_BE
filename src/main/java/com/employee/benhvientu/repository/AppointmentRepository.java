package com.employee.benhvientu.repository;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.Department;
import com.employee.benhvientu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByUser(User user);
    Appointment findByAppointmentIdAndUserUsername(int appointmentId, String username);
    List<Appointment> findByDepartment(Department department);
    boolean existsByUserAndAppointmentDate(User user, Date appointmentDate);
    
    // Thêm các method mới cho AssistantService
    // Sửa: Sử dụng department.departmentId thay vì departmentId
    List<Appointment> findByDepartment_DepartmentIdAndStatus(Integer departmentId, String status);
    List<Appointment> findByDoctorUserIdAndStatus(Integer doctorId, String status);
    List<Appointment> findByDepartment_DepartmentId(Integer departmentId);
    List<Appointment> findByStatus(String status);
    
    // Tìm lịch hẹn đã xác nhận giữa bác sĩ và bệnh nhân cụ thể
    List<Appointment> findByDoctorUserIdAndUserUserIdAndStatus(Integer doctorId, Integer patientId, String status);
}