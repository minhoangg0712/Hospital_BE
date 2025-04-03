package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.MedicineDTO;
import com.employee.benhvientu.entity.Medicine;
import com.employee.benhvientu.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    // Dữ liệu mẫu
    private Medicine testMedicine1;
    private Medicine testMedicine2;

    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu mẫu cho test
        testMedicine1 = new Medicine();
        testMedicine1.setMedicineId(1L);
        testMedicine1.setName("Paracetamol");
        testMedicine1.setDescription("Thuốc giảm đau, hạ sốt");
        testMedicine1.setUnitPrice(new BigDecimal("15000"));
        testMedicine1.setCreatedAt(LocalDateTime.now());

        testMedicine2 = new Medicine();
        testMedicine2.setMedicineId(2L);
        testMedicine2.setName("Amoxicillin");
        testMedicine2.setDescription("Kháng sinh");
        testMedicine2.setUnitPrice(new BigDecimal("25000"));
        testMedicine2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test lấy danh sách tất cả thuốc")
    void testGetAllMedicines() {
        // Arrange - Chuẩn bị dữ liệu
        List<Medicine> medicines = Arrays.asList(testMedicine1, testMedicine2);
        when(medicineRepository.findAll()).thenReturn(medicines);

        // Act - Thực hiện thao tác cần test
        List<MedicineDTO> result = medicineService.getAllMedicines();

        // Assert - Kiểm tra kết quả
        assertEquals(2, result.size(), "Danh sách thuốc nên có 2 phần tử");
        assertEquals("Paracetamol", result.get(0).getName(), "Tên thuốc thứ nhất phải là Paracetamol");
        assertEquals("Amoxicillin", result.get(1).getName(), "Tên thuốc thứ hai phải là Amoxicillin");
        assertEquals(new BigDecimal("15000"), result.get(0).getUnitPrice(), "Giá thuốc thứ nhất phải là 15000");
        assertEquals(new BigDecimal("25000"), result.get(1).getUnitPrice(), "Giá thuốc thứ hai phải là 25000");
        assertEquals("Thuốc giảm đau, hạ sốt", result.get(0).getDescription(), "Mô tả thuốc thứ nhất phải chính xác");
        assertEquals("Kháng sinh", result.get(1).getDescription(), "Mô tả thuốc thứ hai phải chính xác");

        // Verify - Xác minh rằng repository đã được gọi đúng cách
        verify(medicineRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test lấy danh sách thuốc trống")
    void testGetEmptyMedicineList() {
        // Arrange
        when(medicineRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<MedicineDTO> result = medicineService.getAllMedicines();

        // Assert
        assertEquals(0, result.size(), "Danh sách thuốc nên trống");

        // Verify
        verify(medicineRepository, times(1)).findAll();
    }
}