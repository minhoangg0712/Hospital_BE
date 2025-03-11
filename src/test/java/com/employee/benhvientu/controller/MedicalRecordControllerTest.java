package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.MedicalRecordDTO;
import com.employee.benhvientu.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    // Trường hợp thành công - Bác sĩ truy cập danh sách hồ sơ bệnh án của bệnh nhân cùng phòng ban
    @Test
    void testGetMedicalRecords_Success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");
        when(medicalRecordService.getMedicalRecords("doctorUser")).thenReturn(List.of(new MedicalRecordDTO()));
        assertEquals(1, medicalRecordController.getMedicalRecords(auth).size());
    }

    // Trường hợp thành công - Không có hồ sơ bệnh án nào trả về
    @Test
    void testGetMedicalRecords_EmptyList() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");
        when(medicalRecordService.getMedicalRecords("doctorUser")).thenReturn(List.of());
        assertTrue(medicalRecordController.getMedicalRecords(auth).isEmpty());
    }

    // Trường hợp thành công - Bác sĩ tạo mới hồ sơ bệnh án
    @Test
    void testCreateMedicalRecord_Success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(1L);
        request.setReason("Routine checkup");

        MedicalRecordDTO expectedRecord = new MedicalRecordDTO();
        expectedRecord.setReason("Routine checkup");

        when(medicalRecordService.createMedicalRecord("doctorUser", request)).thenReturn(expectedRecord);

        MedicalRecordDTO actualRecord = medicalRecordController.createMedicalRecord(request, auth);

        assertEquals(expectedRecord.getReason(), actualRecord.getReason());
    }

    // Trường hợp lỗi - Patient ID không hợp lệ
    @Test
    void testCreateMedicalRecord_InvalidPatientId() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setReason("Routine checkup");

        when(medicalRecordService.createMedicalRecord("doctorUser", request))
                .thenThrow(new RuntimeException("Invalid patient ID"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.createMedicalRecord(request, auth);
        });

        assertEquals("Invalid patient ID", exception.getMessage());
    }

    // Trường hợp lỗi - Bác sĩ không có quyền tạo hồ sơ bệnh án
    @Test
    void testCreateMedicalRecord_UnauthorizedDoctor() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unauthorizedUser");

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(1L);
        request.setReason("Routine checkup");

        when(medicalRecordService.createMedicalRecord("unauthorizedUser", request))
                .thenThrow(new RuntimeException("Unauthorized doctor"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.createMedicalRecord(request, auth);
        });

        assertEquals("Unauthorized doctor", exception.getMessage());
    }


    // Trường hợp thành công - Lấy hồ sơ bệnh án theo ID khi là bác sĩ có quyền
    @Test
    void testGetMedicalRecordById_DoctorSuccess() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");

        Long recordId = 1L;
        MedicalRecordDTO expectedRecord = new MedicalRecordDTO();
        expectedRecord.setReason("Fever");

        when(medicalRecordService.getMedicalRecordById(recordId, "doctorUser")).thenReturn(expectedRecord);

        MedicalRecordDTO actualRecord = medicalRecordController.getMedicalRecordById(recordId, auth);

        assertEquals(expectedRecord.getReason(), actualRecord.getReason());
        verify(medicalRecordService, times(1)).getMedicalRecordById(recordId, "doctorUser");
    }

    // Trường hợp thành công - Lấy hồ sơ bệnh án theo ID khi là bệnh nhân xem record của mình
    @Test
    void testGetMedicalRecordById_PatientSuccess() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("patientUser");

        Long recordId = 1L;
        MedicalRecordDTO expectedRecord = new MedicalRecordDTO();
        expectedRecord.setReason("Headache");

        when(medicalRecordService.getMedicalRecordById(recordId, "patientUser")).thenReturn(expectedRecord);

        MedicalRecordDTO actualRecord = medicalRecordController.getMedicalRecordById(recordId, auth);

        assertEquals(expectedRecord.getReason(), actualRecord.getReason());
        verify(medicalRecordService, times(1)).getMedicalRecordById(recordId, "patientUser");
    }

    // Trường hợp thành công - Lấy hồ sơ bệnh án theo ID khi là admin
    @Test
    void testGetMedicalRecordById_AdminSuccess() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("adminUser");

        Long recordId = 1L;
        MedicalRecordDTO expectedRecord = new MedicalRecordDTO();
        expectedRecord.setReason("Annual checkup");

        when(medicalRecordService.getMedicalRecordById(recordId, "adminUser")).thenReturn(expectedRecord);

        MedicalRecordDTO actualRecord = medicalRecordController.getMedicalRecordById(recordId, auth);

        assertEquals(expectedRecord.getReason(), actualRecord.getReason());
        verify(medicalRecordService, times(1)).getMedicalRecordById(recordId, "adminUser");
    }

    // Trường hợp lỗi - hồ sơ bệnh án không tồn tại
    @Test
    void testGetMedicalRecordById_RecordNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");

        Long recordId = 999L;

        when(medicalRecordService.getMedicalRecordById(recordId, "doctorUser"))
                .thenThrow(new RuntimeException("Medical record not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.getMedicalRecordById(recordId, auth);
        });

        assertEquals("Medical record not found", exception.getMessage());
    }

    // Trường hợp lỗi - Bệnh nhân cố gắng truy cập hồ sơ bệnh án của người khác
    @Test
    void testGetMedicalRecordById_PatientUnauthorized() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("patientUser");

        Long recordId = 2L;

        when(medicalRecordService.getMedicalRecordById(recordId, "patientUser"))
                .thenThrow(new RuntimeException("Bệnh nhân chỉ được xem medical records của bản thân"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.getMedicalRecordById(recordId, auth);
        });

        assertEquals("Bệnh nhân chỉ được xem medical records của bản thân", exception.getMessage());
    }

    // Trường hợp lỗi - Bác sĩ cố gắng truy cập hồ sơ bệnh án của bệnh nhân khác khoa
    @Test
    void testGetMedicalRecordById_DoctorDifferentDepartment() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");

        Long recordId = 3L;

        when(medicalRecordService.getMedicalRecordById(recordId, "doctorUser"))
                .thenThrow(new RuntimeException("Bác sĩ chỉ xem được medical records của bệnh nhân cùng department"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.getMedicalRecordById(recordId, auth);
        });

        assertEquals("Bác sĩ chỉ xem được medical records của bệnh nhân cùng department", exception.getMessage());
    }

    // Trường hợp lỗi - User không tồn tại khi tạo hồ sơ bệnh án
    @Test
    void testCreateMedicalRecord_UserNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("nonExistentUser");

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(1L);

        when(medicalRecordService.createMedicalRecord("nonExistentUser", request))
                .thenThrow(new RuntimeException("Không tìm thấy bác sĩ"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.createMedicalRecord(request, auth);
        });

        assertEquals("Không tìm thấy bác sĩ", exception.getMessage());
    }

    // Trường hợp lỗi - Bác sĩ và bệnh nhân không cùng khoa
    @Test
    void testCreateMedicalRecord_DifferentDepartments() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("doctorUser");

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(5L);

        when(medicalRecordService.createMedicalRecord("doctorUser", request))
                .thenThrow(new RuntimeException("Bác sĩ chỉ có thể tạo hồ sơ cho bệnh nhân trong cùng một khoa"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.createMedicalRecord(request, auth);
        });

        assertEquals("Bác sĩ chỉ có thể tạo hồ sơ cho bệnh nhân trong cùng một khoa", exception.getMessage());
    }

    // Trường hợp lỗi - Truy cập danh sách hồ sơ bệnh án khi không có quyền
    @Test
    void testGetMedicalRecords_Unauthorized() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unknownRoleUser");

        when(medicalRecordService.getMedicalRecords("unknownRoleUser"))
                .thenThrow(new RuntimeException("Unauthorized access"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.getMedicalRecords(auth);
        });

        assertEquals("Unauthorized access", exception.getMessage());
    }

    // Trường hợp lỗi - User repository trả về null
    @Test
    void testGetMedicalRecords_NullUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(null);

        when(medicalRecordService.getMedicalRecords(null))
                .thenThrow(new RuntimeException("User not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.getMedicalRecords(auth);
        });

        assertEquals("User not found", exception.getMessage());
    }
}