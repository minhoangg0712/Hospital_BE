package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    // Trường hợp thành công - Bệnh nhân truy cập danh sách hồ sơ của chính họ
    @Test
    void testGetPatientProfiles_AsPatient() {
        mockAuthentication("patient");
        when(userService.getPatientProfiles("patient")).thenReturn(List.of(userDTO()));
        assertEquals(1, userController.getPatientProfiles().size());
    }

    // Trường hợp thành công - Bác sĩ truy cập danh sách hồ sơ bệnh nhân cùng phòng ban
    @Test
    void testGetPatientProfiles_AsDoctor() {
        mockAuthentication("doctor");
        when(userService.getPatientProfiles("doctor")).thenReturn(List.of(userDTO(), userDTO()));
        assertEquals(2, userController.getPatientProfiles().size());
    }

    // Trường hợp thành công - Admin truy cập danh sách toàn bộ hồ sơ
    @Test
    void testGetPatientProfiles_AsAdmin() {
        mockAuthentication("admin");
        when(userService.getPatientProfiles("admin")).thenReturn(List.of(userDTO(), userDTO(), userDTO()));
        assertEquals(3, userController.getPatientProfiles().size());
    }

    // Trường hợp lỗi - Chưa xác thực
    @Test
    void testGetPatientProfiles_Unauthenticated() {
        SecurityContextHolder.clearContext();
        assertThrows(RuntimeException.class, () -> userController.getPatientProfiles());
    }

    // Trường hợp thành công - Bệnh nhân truy cập hồ sơ của chính họ
    @Test
    void testGetUserProfileById_AsPatient_Success() {
        mockAuthentication("patient");
        when(userService.getUserProfileById(1L, "patient")).thenReturn(userDTO());
        assertNotNull(userController.getUserProfileById(1L, SecurityContextHolder.getContext().getAuthentication()));
    }

    // Trường hợp lỗi - Bệnh nhân truy cập hồ sơ người khác
    @Test
    void testGetUserProfileById_AsPatient_AccessDenied() {
        mockAuthentication("patient");
        when(userService.getUserProfileById(2L, "patient")).thenThrow(new AccessDeniedException("Access Denied"));
        assertThrows(AccessDeniedException.class, () -> userController.getUserProfileById(2L, SecurityContextHolder.getContext().getAuthentication()));
    }

    // Trường hợp thành công - Bác sĩ truy cập hồ sơ bệnh nhân cùng phòng ban
    @Test
    void testGetUserProfileById_AsDoctor_Success() {
        mockAuthentication("doctor");
        when(userService.getUserProfileById(2L, "doctor")).thenReturn(userDTO());
        assertNotNull(userController.getUserProfileById(2L, SecurityContextHolder.getContext().getAuthentication()));
    }

    // Trường hợp lỗi - Bác sĩ truy cập hồ sơ ngoài phòng ban hoặc hồ sơ bác sĩ khác
    @Test
    void testGetUserProfileById_AsDoctor_AccessDenied() {
        mockAuthentication("doctor");
        when(userService.getUserProfileById(3L, "doctor")).thenThrow(new AccessDeniedException("Access Denied"));
        assertThrows(AccessDeniedException.class, () -> userController.getUserProfileById(3L, SecurityContextHolder.getContext().getAuthentication()));
    }

    // Trường hợp thành công - Admin truy cập bất kỳ hồ sơ nào
    @Test
    void testGetUserProfileById_AsAdmin() {
        mockAuthentication("admin");
        when(userService.getUserProfileById(3L, "admin")).thenReturn(userDTO());
        assertNotNull(userController.getUserProfileById(3L, SecurityContextHolder.getContext().getAuthentication()));
    }

    // Trường hợp lỗi - Hồ sơ không tồn tại
    @Test
    void testGetUserProfileById_NotFound() {
        mockAuthentication("admin");
        when(userService.getUserProfileById(99L, "admin")).thenThrow(new RuntimeException("Target user not found"));
        assertThrows(RuntimeException.class, () -> userController.getUserProfileById(99L, SecurityContextHolder.getContext().getAuthentication()));
    }

    private void mockAuthentication(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
    }

    private UserDTO userDTO() {
        UserDTO dto = new UserDTO();
        dto.setUserId(1L);
        dto.setName("Patient Name");
        dto.setEmail("patient@example.com");
        dto.setPhone("0123456789");
        dto.setCccd("123456789012");
        dto.setInsuranceNumber("BHYT123456");
        dto.setAddress("123 Main St");
        return dto;
    }
}