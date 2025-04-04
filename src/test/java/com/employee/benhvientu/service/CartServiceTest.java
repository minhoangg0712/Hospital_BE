package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.CartItemDTO;
import com.employee.benhvientu.dto.CartResponseDTO;
import com.employee.benhvientu.entity.CartItem;
import com.employee.benhvientu.entity.Medicine;
import com.employee.benhvientu.entity.User;
import com.employee.benhvientu.repository.CartItemRepository;
import com.employee.benhvientu.repository.MedicineRepository;
import com.employee.benhvientu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private CartService cartService;

    // Dữ liệu mẫu
    private User testUser;
    private Medicine testMedicine;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu mẫu cho test
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        testMedicine = new Medicine();
        testMedicine.setMedicineId(1L);
        testMedicine.setName("Paracetamol");
        testMedicine.setDescription("Thuốc giảm đau, hạ sốt");
        testMedicine.setUnitPrice(new BigDecimal("15000"));
        testMedicine.setCreatedAt(LocalDateTime.now());

        testCartItem = new CartItem();
        testCartItem.setCartItemId(1L);
        testCartItem.setUser(testUser);
        testCartItem.setMedicine(testMedicine);
        testCartItem.setQuantity(2);
        testCartItem.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test thêm sản phẩm mới vào giỏ hàng")
    void testAddNewItemToCart() {
        // Arrange - Chuẩn bị dữ liệu test
        String username = "testuser";
        Long medicineId = 1L;
        Integer quantity = 2;
        String role = "ROLE_EMP";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(testMedicine));
        when(cartItemRepository.findByUserAndMedicine_MedicineId(testUser, medicineId)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            savedItem.setCartItemId(1L);
            return savedItem;
        });

        // Act - Thực hiện phương thức cần test
        CartItemDTO result = cartService.addToCart(username, medicineId, quantity, role);

        // Assert - Kiểm tra kết quả
        assertNotNull(result, "Kết quả không được null");
        assertEquals(1L, result.getMedicineId(), "ID thuốc phải là 1");
        assertEquals("Paracetamol", result.getMedicineName(), "Tên thuốc phải là Paracetamol");
        assertEquals(2, result.getQuantity(), "Số lượng phải là 2");
        assertEquals(new BigDecimal("30000"), result.getTotalPrice(), "Tổng giá phải là 30000");

        // Verify - Xác minh rằng repository đã được gọi đúng cách
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Test thêm sản phẩm đã tồn tại trong giỏ hàng")
    void testAddExistingItemToCart() {
        // Arrange
        String username = "testuser";
        Long medicineId = 1L;
        Integer quantity = 3;
        String role = "ROLE_EMP";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(testMedicine));
        when(cartItemRepository.findByUserAndMedicine_MedicineId(testUser, medicineId)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartItemDTO result = cartService.addToCart(username, medicineId, quantity, role);

        // Assert
        assertNotNull(result, "Kết quả không được null");
        assertEquals(5, result.getQuantity(), "Số lượng phải là 5 (2 cũ + 3 mới)");
        assertEquals(new BigDecimal("75000"), result.getTotalPrice(), "Tổng giá phải là 75000");

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Test thêm vào giỏ hàng với số lượng không hợp lệ")
    void testAddToCartWithInvalidQuantity() {
        // Arrange
        String username = "testuser";
        Long medicineId = 1L;
        Integer quantity = 0;
        String role = "ROLE_EMP";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(username, medicineId, quantity, role);
        });

        assertEquals("Số lượng phải lớn hơn 0", exception.getMessage());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Test thêm vào giỏ hàng với quyền không hợp lệ")
    void testAddToCartWithInvalidRole() {
        // Arrange
        String username = "testuser";
        Long medicineId = 1L;
        Integer quantity = 2;
        String role = "ROLE_ADMIN";

        // Act & Assert
        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            cartService.addToCart(username, medicineId, quantity, role);
        });

        assertEquals("Chỉ bệnh nhân và bác sĩ mới được sử dụng giỏ hàng", exception.getMessage());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Test lấy thông tin giỏ hàng với tổng giá trị")
    void testGetCartWithSummary() {
        // Arrange
        String username = "testuser";
        String role = "ROLE_EMP";
        List<CartItem> cartItems = Arrays.asList(testCartItem);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findByUser(testUser)).thenReturn(cartItems);

        // Act
        CartResponseDTO result = cartService.getCartWithSummary(username, role);

        // Assert
        assertEquals(1, result.getItems().size(), "Giỏ hàng nên có 1 phần tử");
        assertEquals(1L, result.getItems().get(0).getMedicineId(), "ID thuốc phải là 1");
        assertEquals("Paracetamol", result.getItems().get(0).getMedicineName(), "Tên thuốc phải là Paracetamol");
        assertEquals(2, result.getItems().get(0).getQuantity(), "Số lượng phải là 2");
        assertEquals(new BigDecimal("30000"), result.getItems().get(0).getTotalPrice(), "Tổng giá phải là 30000");
        verify(cartItemRepository, times(1)).findByUser(testUser);
    }

    @Test
    @DisplayName("Test lấy danh sách các item trong giỏ hàng")
    void testGetCartItems() {
        // Arrange
        String username = "testuser";
        String role = "ROLE_EMP";
        List<CartItem> cartItems = Arrays.asList(testCartItem);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findByUser(testUser)).thenReturn(cartItems);

        // Act
        List<CartItemDTO> result = cartService.getCartItems(username, role);

        // Assert
        assertEquals(1, result.size(), "Danh sách giỏ hàng nên có 1 phần tử");
        assertEquals(1L, result.get(0).getMedicineId(), "ID thuốc phải là 1");
        assertEquals("Paracetamol", result.get(0).getMedicineName(), "Tên thuốc phải là Paracetamol");
        assertEquals(2, result.get(0).getQuantity(), "Số lượng phải là 2");

        verify(cartItemRepository, times(1)).findByUser(testUser);
    }

    @Test
    @DisplayName("Test xóa item khỏi giỏ hàng")
    void testRemoveFromCart() {
        // Arrange
        String username = "testuser";
        Long cartItemId = 1L;
        String role = "ROLE_EMP";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(testCartItem));

        // Act
        cartService.removeFromCart(username, cartItemId, role);

        // Assert
        verify(cartItemRepository, times(1)).delete(testCartItem);
    }


    @Test
    @DisplayName("Test cập nhật số lượng item trong giỏ hàng")
    void testUpdateQuantity() {
        // Arrange
        String username = "testuser";
        Long cartItemId = 1L;
        Integer newQuantity = 5;

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartItemDTO result = cartService.updateQuantity(username, cartItemId, newQuantity);

        // Assert
        assertNotNull(result, "Kết quả không được null");
        assertEquals(5, result.getQuantity(), "Số lượng phải được cập nhật thành 5");
        assertEquals(new BigDecimal("75000"), result.getTotalPrice(), "Tổng giá phải là 75000");

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }
//Lỗi
    @Test
    @DisplayName("Test cập nhật số lượng với giá trị không hợp lệ")
    void testUpdateQuantityWithInvalidValue() {
        // Arrange
        String username = "testuser";
        Long cartItemId = 1L;
        Integer newQuantity = 0;

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateQuantity(username, cartItemId, newQuantity);
        });

        assertEquals("Số lượng phải lớn hơn 0", exception.getMessage());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Test cập nhật số lượng item của người dùng khác")
    void testUpdateQuantityWithDifferentUser() {
        // Arrange
        String username = "testuser";
        Long cartItemId = 1L;
        Integer newQuantity = 5;

        User differentUser = new User();
        differentUser.setUserId(2L);
        differentUser.setUsername("testuser");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(differentUser));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(testCartItem));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateQuantity(username, cartItemId, newQuantity);
        });

        assertEquals("Không có quyền cập nhật item này", exception.getMessage());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }
    @Test
    @DisplayName("Test thêm sản phẩm không tồn tại vào giỏ hàng")
    void testAddNonExistentMedicineToCart() {
        // Arrange
        String username = "testuser";
        Long medicineId = 999L;
        Integer quantity = 2;
        String role = "ROLE_EMP";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(username, medicineId, quantity, role);
        });

        assertEquals("Không tìm thấy thuốc", exception.getMessage());
    }

}