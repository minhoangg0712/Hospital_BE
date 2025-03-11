package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
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
            throw new RuntimeException("Không tìm thấy người dùng trong hệ thống!");
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
    public UserDTO getUserProfileById(Long id, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại!"));

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng cần truy cập!"));

        if (currentUser.getRoleCode().equals("ADM")) {
            return convertToDTO(targetUser);
        }

        if (currentUser.getRoleCode().equals("MGR")) {
            if (targetUser.getRoleCode().equals("EMP") && currentUser.getDepartmentId().equals(targetUser.getDepartmentId())) {
                return convertToDTO(targetUser);
            } else {
                throw new RuntimeException("Bác sĩ chỉ được xem hồ sơ của bệnh nhân trong cùng khoa!");
            }
        }

        if (currentUser.getRoleCode().equals("EMP")) {
            if (!currentUser.getUserId().equals(targetUser.getUserId())) {
                throw new RuntimeException("Bệnh nhân chỉ được xem hồ sơ của chính mình!");
            }
            return convertToDTO(targetUser);
        }

        throw new RuntimeException("Truy cập trái phép!");
    }



    public UserDTO updatePatientProfile(String username, UserDTO userDTO) {
        Optional<User> currentUserOptional = userRepository.findByUsername(username);
        if (currentUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng trong hệ thống!");
        }

        User currentUser = currentUserOptional.get();
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ!"));

        // Kiểm tra quyền hạn
        if ("EMP".equals(currentUser.getRoleCode()) && !currentUser.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa hồ sơ này!");
        }
        if ("ADM".equals(currentUser.getRoleCode()) && !"MGR".equals(user.getRoleCode())) {
            throw new AccessDeniedException("Admin chỉ có thể chỉnh sửa hồ sơ của bác sĩ!");
        }
        if ("MGR".equals(currentUser.getRoleCode())) {
            throw new AccessDeniedException("Bác sĩ không có quyền cập nhật hồ sơ!");
        }

        // Kiểm tra dữ liệu hợp lệ
        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên không được để trống");
        }
        if (userDTO.getEmail() == null || !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không hợp lệ");
        }
        if (userDTO.getPhone() == null || !userDTO.getPhone().matches("^\\d{10,11}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại không hợp lệ");
        }

        // Kiểm tra nếu CCCD đã tồn tại nhưng không phải của user hiện tại
        Optional<User> existingUserWithCccd = userRepository.findByCccd(userDTO.getCccd());
        if (existingUserWithCccd.isPresent() && !existingUserWithCccd.get().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CCCD đã tồn tại! Vui lòng nhập số khác.");
        }

        // Kiểm tra nếu số bảo hiểm y tế đã tồn tại nhưng không phải của user hiện tại
        Optional<User> existingUserWithInsurance = userRepository.findByInsuranceNumber(userDTO.getInsuranceNumber());
        if (existingUserWithInsurance.isPresent() && !existingUserWithInsurance.get().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số bảo hiểm y tế đã tồn tại! Vui lòng nhập số khác.");
        }

        // Cập nhật thông tin
        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setGender(User.Gender.valueOf(userDTO.getGender()));
        user.setAddress(userDTO.getAddress());

        if (!user.getCccd().equals(userDTO.getCccd())) {
            user.setCccd(userDTO.getCccd());
        }
        if (!user.getInsuranceNumber().equals(userDTO.getInsuranceNumber())) {
            user.setInsuranceNumber(userDTO.getInsuranceNumber());
        }

        userRepository.save(user);
        return convertToDTO(user);
    }



    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName() != null ? user.getName() : ""); // Tránh null
        dto.setPhone(user.getPhone() != null ? user.getPhone() : "");
        dto.setEmail(user.getEmail() != null ? user.getEmail() : "");
        dto.setGender(user.getGender() != null ? user.getGender().name() : "Other");
        dto.setRoleCode(user.getRoleCode() != null ? user.getRoleCode() : "");
        dto.setDepartmentId(user.getDepartmentId());
        dto.setCccd(user.getCccd() != null ? user.getCccd() : "");
        dto.setInsuranceNumber(user.getInsuranceNumber() != null ? user.getInsuranceNumber() : "");
        dto.setAddress(user.getAddress() != null ? user.getAddress() : "");
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
