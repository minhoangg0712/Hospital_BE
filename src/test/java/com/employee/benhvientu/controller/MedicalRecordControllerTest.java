package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.MedicalRecordDTO;
import com.employee.benhvientu.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    @Test
    void testGetMedicalRecords_Success() {
        // Given
        String username = "doctorUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        List<MedicalRecordDTO> expectedRecords = List.of(new MedicalRecordDTO());
        when(medicalRecordService.getMedicalRecords(username)).thenReturn(expectedRecords);

        // When
        List<MedicalRecordDTO> actualRecords = medicalRecordController.getMedicalRecords(authentication);

        // Then
        assertEquals(expectedRecords.size(), actualRecords.size());
    }

    @Test
    void testGetMedicalRecords_EmptyList() {
        // Given
        String username = "doctorUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        when(medicalRecordService.getMedicalRecords(username)).thenReturn(List.of());

        // When
        List<MedicalRecordDTO> actualRecords = medicalRecordController.getMedicalRecords(authentication);

        // Then
        assertTrue(actualRecords.isEmpty());
    }

    @Test
    void testCreateMedicalRecord_Success() {
        // Given
        String username = "doctorUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(1);
        request.setReason("Routine checkup");
        request.setDiagnosis("Flu");

        MedicalRecordDTO expectedRecord = new MedicalRecordDTO();
        expectedRecord.setReason("Flu");

        when(medicalRecordService.createMedicalRecord(username, request)).thenReturn(expectedRecord);

        // When
        MedicalRecordDTO actualRecord = medicalRecordController.createMedicalRecord(request, authentication);

        // Then
        assertEquals(expectedRecord.getReason(), actualRecord.getReason());
    }

    @Test
    void testCreateMedicalRecord_InvalidPatientId() {
        // Given
        String username = "doctorUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(null);
        request.setReason("Routine checkup");

        when(medicalRecordService.createMedicalRecord(username, request))
                .thenThrow(new RuntimeException("Invalid patient ID"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.createMedicalRecord(request, authentication);
        });

        assertEquals("Invalid patient ID", exception.getMessage());
    }

    @Test
    void testCreateMedicalRecord_UnauthorizedDoctor() {
        // Given
        String username = "unauthorizedUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        MedicalRecordDTO request = new MedicalRecordDTO();
        request.setPatientId(1);
        request.setReason("Routine checkup");

        when(medicalRecordService.createMedicalRecord(username, request))
                .thenThrow(new RuntimeException("Unauthorized doctor"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            medicalRecordController.createMedicalRecord(request, authentication);
        });

        assertEquals("Unauthorized doctor", exception.getMessage());
    }
}
