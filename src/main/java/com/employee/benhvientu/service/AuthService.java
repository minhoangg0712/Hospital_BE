package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.RegisterRequest;
import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.UserRepository;
import com.employee.benhvientu.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Người dùng '{}' không tồn tại!", username);
                    return new RuntimeException("Người dùng không tồn tại!");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Đăng nhập thất bại cho user '{}': Mật khẩu không chính xác!", username);
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRoleCode(), user.getUserId());
    }

    public UserDTO registerUser(RegisterRequest request) {
        // Kiểm tra request không được chứa giá trị null
        if (request == null || request.getUsername() == null || request.getPassword() == null ||
                request.getEmail() == null || request.getPhone() == null || request.getCccd() == null ||
                request.getInsuranceNumber() == null || request.getAddress() == null) {
            throw new RuntimeException("Thiếu thông tin đăng ký!");
        }

        // Kiểm tra email hợp lệ
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Email không hợp lệ!");
        }

        // Kiểm tra trùng lặp trong một bước để tối ưu truy vấn DB
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent() ||
                userRepository.findByPhone(request.getPhone()).isPresent() ||
                userRepository.findByCccd(request.getCccd()).isPresent() ||
                userRepository.findByInsuranceNumber(request.getInsuranceNumber()).isPresent()) {
            throw new RuntimeException("Thông tin đăng ký đã tồn tại trong hệ thống!");
        }

        // Tạo đối tượng User mới
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Mã hóa mật khẩu
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setGender(User.Gender.valueOf(request.getGender()));
        user.setRoleCode("EMP"); // Chỉ cho phép đăng ký với vai trò EMP (Bệnh nhân)
        user.setDepartmentId(null); // Không có phòng ban

        // Thêm thông tin mới
        user.setCccd(request.getCccd());
        user.setInsuranceNumber(request.getInsuranceNumber());
        user.setAddress(request.getAddress());

        // Lưu vào database
        userRepository.save(user);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender().name());
        dto.setRoleCode(user.getRoleCode());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setCccd(user.getCccd());
        dto.setInsuranceNumber(user.getInsuranceNumber());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
