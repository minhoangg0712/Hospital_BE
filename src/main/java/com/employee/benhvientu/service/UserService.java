package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách hồ sơ dựa theo quyền hạn
    public List<UserDTO> getPatientProfiles(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found in database");
        }

        User currentUser = userOptional.get();
        List<User> users;

        // Nếu là bệnh nhân -> Chỉ xem được hồ sơ của chính họ
        if ("EMP".equals(currentUser.getRoleCode())) {
            users = List.of(currentUser);
        }
        // Nếu là bác sĩ -> Chỉ xem được hồ sơ của bệnh nhân cùng `department_id`, nhưng không xem được hồ sơ của chính mình
        else if ("MGR".equals(currentUser.getRoleCode())) {
            users = userRepository.findByRoleCodeAndDepartmentId("EMP", currentUser.getDepartmentId());
        }
        // Nếu là Admin -> Xem được tất cả hồ sơ bệnh nhân và bác sĩ
        else {
            users = userRepository.findAll();
        }

        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public UserDTO updatePatientProfile(String username, UserDTO userDTO) {
        Optional<User> currentUserOptional = userRepository.findByUsername(username);
        if (currentUserOptional.isEmpty()) {
            throw new RuntimeException("User not found in database");
        }

        User currentUser = currentUserOptional.get();
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ!"));

        // Bệnh nhân chỉ được sửa hồ sơ của chính mình
        if ("EMP".equals(currentUser.getRoleCode())) {
            if (!currentUser.getUserId().equals(user.getUserId())) {
                throw new RuntimeException("Bạn không có quyền chỉnh sửa hồ sơ này!");
            }
        }
        // Admin có thể sửa hồ sơ của bác sĩ
        else if ("ADM".equals(currentUser.getRoleCode())) {
            if (!"MGR".equals(user.getRoleCode())) {
                throw new RuntimeException("Admin chỉ có thể chỉnh sửa hồ sơ của bác sĩ!");
            }
        }
        // Bác sĩ không được sửa bất kỳ hồ sơ nào
        else {
            throw new RuntimeException("Bác sĩ không có quyền cập nhật hồ sơ!");
        }

        // Kiểm tra nếu CCCD đã tồn tại trong database nhưng không phải của chính user đó
        Optional<User> existingUserWithCccd = userRepository.findByCccd(userDTO.getCccd());
        if (existingUserWithCccd.isPresent() && !existingUserWithCccd.get().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("CCCD đã tồn tại! Vui lòng nhập số khác.");
        }

        // Kiểm tra nếu số bảo hiểm y tế đã tồn tại trong database nhưng không phải của chính user đó
        Optional<User> existingUserWithInsurance = userRepository.findByInsuranceNumber(userDTO.getInsuranceNumber());
        if (existingUserWithInsurance.isPresent() && !existingUserWithInsurance.get().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Số bảo hiểm y tế đã tồn tại! Vui lòng nhập số khác.");
        }

        // Chỉ cập nhật nếu giá trị mới khác với giá trị hiện tại
        if (!user.getCccd().equals(userDTO.getCccd())) {
            user.setCccd(userDTO.getCccd());
        }

        if (!user.getInsuranceNumber().equals(userDTO.getInsuranceNumber())) {
            user.setInsuranceNumber(userDTO.getInsuranceNumber());
        }

        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setGender(User.Gender.valueOf(userDTO.getGender()));
        user.setAddress(userDTO.getAddress());

        // Lưu thay đổi
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
