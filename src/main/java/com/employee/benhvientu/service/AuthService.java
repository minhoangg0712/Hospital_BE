package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.RegisterRequest;
import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.UserRepository;
import com.employee.benhvientu.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRoleCode(), user.getUserId());
    }

    // Cập nhật phương thức đăng ký người dùng mới
    public UserDTO registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        if (userRepository.findByCccd(request.getCccd()).isPresent()) {
            throw new RuntimeException("Căn cước công dân đã tồn tại!");
        }

        if (userRepository.findByInsuranceNumber(request.getInsuranceNumber()).isPresent()) {
            throw new RuntimeException("Số bảo hiểm y tế đã tồn tại!");
        }

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
