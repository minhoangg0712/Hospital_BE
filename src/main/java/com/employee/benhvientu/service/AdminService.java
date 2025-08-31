package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.CreateDoctorRequest;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Map<String, String> createDoctorAccount(CreateDoctorRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại trong hệ thống");
        }

        // Tạo tài khoản bác sĩ mới với thông tin cơ bản
        User newDoctor = new User();
        newDoctor.setUsername(request.getUsername());
        newDoctor.setPassword(passwordEncoder.encode(request.getPassword()));
        newDoctor.setRoleCode("MGR"); // Set role là bác sĩ

        // Thiết lập departmentId từ request
        newDoctor.setDepartmentId(request.getDepartmentId());

        // Tạo số điện thoại tạm thời duy nhất
        String tempPhone = generateUniqueTempPhone();

        // Đặt giá trị mặc định cho các trường bắt buộc
        newDoctor.setName("Doctor_" + request.getUsername()); // Tên tạm thời
        newDoctor.setPhone(tempPhone); // Số điện thoại tạm thời
        newDoctor.setEmail(request.getUsername() + "@temp.com"); // Email tạm thời
        newDoctor.setAddress("Chưa cập nhật"); // Địa chỉ tạm thời
        newDoctor.setCccd("TEMP" + System.currentTimeMillis()); // CCCD tạm thời
        newDoctor.setInsuranceNumber("INS" + System.currentTimeMillis()); // Số bảo hiểm tạm thời
        newDoctor.setGender(User.Gender.Other); // Giới tính mặc định
        newDoctor.setCreatedAt(LocalDateTime.now());
        newDoctor.setUpdatedAt(LocalDateTime.now());

        // Lưu vào database
        userRepository.save(newDoctor);

        return Map.of(
                "message", "Tạo tài khoản bác sĩ thành công",
                "username", request.getUsername(),
                "departmentId", request.getDepartmentId() != null ? request.getDepartmentId().toString() : "Không có"
        );
    }

    public Map<String, String> createAssistantAccount(CreateDoctorRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại trong hệ thống");
        }

        // Tạo tài khoản phụ tá mới với thông tin cơ bản
        User newAssistant = new User();
        newAssistant.setUsername(request.getUsername());
        newAssistant.setPassword(passwordEncoder.encode(request.getPassword()));
        newAssistant.setRoleCode("AST"); // Set role là phụ tá

        // Thiết lập departmentId từ request
        newAssistant.setDepartmentId(request.getDepartmentId());

        // Tạo số điện thoại tạm thời duy nhất
        String tempPhone = generateUniqueTempPhone();

        // Đặt giá trị mặc định cho các trường bắt buộc
        newAssistant.setName("Assistant_" + request.getUsername()); // Tên tạm thời
        newAssistant.setPhone(tempPhone); // Số điện thoại tạm thời
        newAssistant.setEmail(request.getUsername() + "@temp.com"); // Email tạm thời
        newAssistant.setAddress("Chưa cập nhật"); // Địa chỉ tạm thời
        newAssistant.setCccd("TEMP" + System.currentTimeMillis()); // CCCD tạm thời
        newAssistant.setInsuranceNumber("INS" + System.currentTimeMillis()); // Số bảo hiểm tạm thời
        newAssistant.setGender(User.Gender.Other); // Giới tính mặc định
        newAssistant.setCreatedAt(LocalDateTime.now());
        newAssistant.setUpdatedAt(LocalDateTime.now());

        // Lưu vào database
        userRepository.save(newAssistant);

        return Map.of(
                "message", "Tạo tài khoản phụ tá thành công",
                "username", request.getUsername(),
                "departmentId", request.getDepartmentId() != null ? request.getDepartmentId().toString() : "Không có"
        );
    }

    private String generateUniqueTempPhone() {
        String tempPhone;
        do {
            // Tạo số điện thoại tạm thời với định dạng: TEMP + 6 chữ số cuối của timestamp
            tempPhone = "TEMP" + (System.currentTimeMillis() % 1000000);
        } while (userRepository.findByPhone(tempPhone).isPresent());
        return tempPhone;
    }
} 