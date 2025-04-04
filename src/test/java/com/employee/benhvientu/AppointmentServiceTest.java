package com.employee.benhvientu;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.entity.Department;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.AppointmentRepository;
import com.employee.benhvientu.repository.DepartmentRepository;
import com.employee.benhvientu.repository.UserRepository;
import com.employee.benhvientu.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User testUser;
    private Department testDepartment;
    private Appointment testAppointment;
    private Date appointmentDate;

    @BeforeEach
    void setUp() {
        // Setup test data
        appointmentDate = new Date();

        testUser = new User();
        testUser.setUsername("testUser");

        testDepartment = new Department();
        testDepartment.setDepartmentId(1);
        testDepartment.setDepartmentName("Cardiology");

        testAppointment = new Appointment();
        testAppointment.setAppointmentId(1);
        testAppointment.setAppointmentDate(appointmentDate);
        testAppointment.setStatus("PENDING");
        testAppointment.setReason("Chest pain");
        testAppointment.setDepartment(testDepartment);
    }

    @Test
    void createAppointment_ForSelf_Success() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.existsByUserAndAppointmentDate(testUser, appointmentDate)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.createAppointment(testAppointment, "testUser", true);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testDepartment, result.getDepartment());
        verify(userRepository).save(testUser);
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void createAppointment_ForRelative_Success() {
        // Arrange
        testAppointment.setRelativeName("John Doe");
        testAppointment.setRelativeIdCard("123456789");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.existsByUserAndAppointmentDate(testUser, appointmentDate)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.createAppointment(testAppointment, "testUser", false);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getRelativeName());
        assertEquals("123456789", result.getRelativeIdCard());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void createAppointment_ForRelative_MissingInfo() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.existsByUserAndAppointmentDate(testUser, appointmentDate)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(testAppointment, "testUser", false);
        });

        assertEquals("Relative name and ID card must be provided for relative appointments.", exception.getMessage());
    }

    @Test
    void createAppointment_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(testAppointment, "nonExistentUser", true);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createAppointment_DepartmentNotFound() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(testAppointment, "testUser", true);
        });

        assertEquals("Department not found", exception.getMessage());
    }

    @Test
    void createAppointment_AlreadyExists() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.existsByUserAndAppointmentDate(testUser, appointmentDate)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(testAppointment, "testUser", true);
        });

        assertEquals("You already have an appointment at this time.", exception.getMessage());
    }

    @Test
    void listAppointments_Success() {
        // Arrange
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(testAppointment);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(appointmentRepository.findByUser(testUser)).thenReturn(appointments);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));

        // Act
        List<Appointment> result = appointmentService.listAppointments("testUser");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getDepartment().getDepartmentName());
    }

    @Test
    void listAppointments_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.listAppointments("nonExistentUser");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void listAppointmentsByDepartment_Success() {
        // Arrange
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(testAppointment);

        User doctor = new User();
        doctor.setUsername("doctor");
        doctor.setDepartmentId(1);

        when(userRepository.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.findByDepartment(testDepartment)).thenReturn(appointments);

        // Act
        List<Appointment> result = appointmentService.listAppointmentsByDepartment("doctor");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment, result.get(0));
    }

    @Test
    void updateAppointment_Success() {
        // Arrange
        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentId(1);
        existingAppointment.setAppointmentDate(new Date(System.currentTimeMillis() - 86400000)); // yesterday
        existingAppointment.setStatus("PENDING");
        existingAppointment.setDepartment(testDepartment);

        when(appointmentRepository.findByAppointmentIdAndUserUsername(1, "testUser")).thenReturn(existingAppointment);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.updateAppointment(1, testAppointment, "testUser");

        // Assert
        assertNotNull(result);
        assertEquals(appointmentDate, result.getAppointmentDate());
        assertEquals("PENDING", result.getStatus());
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    void updateAppointment_NotFound() {
        // Arrange
        when(appointmentRepository.findByAppointmentIdAndUserUsername(1, "testUser")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.updateAppointment(1, testAppointment, "testUser");
        });

        assertEquals("Appointment not found or you do not have permission to update it.", exception.getMessage());
    }

    @Test
    void deleteAppointment_Success() {
        // Arrange
        when(appointmentRepository.findByAppointmentIdAndUserUsername(1, "testUser")).thenReturn(testAppointment);

        // Act
        appointmentService.deleteAppointment(1, "testUser");

        // Assert
        verify(appointmentRepository).delete(testAppointment);
    }

    @Test
    void deleteAppointment_NotFound() {
        // Arrange
        when(appointmentRepository.findByAppointmentIdAndUserUsername(1, "testUser")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.deleteAppointment(1, "testUser");
        });

        assertEquals("Appointment not found or you do not have permission to delete it.", exception.getMessage());
    }
}