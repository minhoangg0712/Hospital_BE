package com.employee.benhvientu.service;

import com.employee.benhvientu.dto.CartItemDTO;
import com.employee.benhvientu.dto.CartResponseDTO;
import com.employee.benhvientu.entity.*;
import com.employee.benhvientu.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private PayOS payOS;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public CartItemDTO addToCart(String username, Long medicineId, Integer quantity, String role) {
        if (!"ROLE_EMP".equals(role) && !"ROLE_MGR".equals(role)) {
            throw new AccessDeniedException("Chỉ bệnh nhân và bác sĩ mới được sử dụng giỏ hàng");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));

        Optional<CartItem> existingItem = cartItemRepository
                .findByUserAndMedicine_MedicineId(user, medicineId);

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setMedicine(medicine);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
        }

        cartItem = cartItemRepository.save(cartItem);
        return convertToDTO(cartItem);
    }
    public CartResponseDTO getCartWithSummary(String username, String role) {
        if (!"ROLE_EMP".equals(role) && !"ROLE_MGR".equals(role)) {
            throw new AccessDeniedException("Chỉ bệnh nhân và bác sĩ mới được xem giỏ hàng");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<CartItemDTO> cartItems = cartItemRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new CartResponseDTO(cartItems);
    }

    public List<CartItemDTO> getCartItems(String username, String role) {
        if (!"ROLE_EMP".equals(role) && !"ROLE_MGR".equals(role)) {
            throw new AccessDeniedException("Chỉ bệnh nhân và bác sĩ mới được xem giỏ hàng");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return cartItemRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void removeFromCart(String username, Long cartItemId, String role) {
        if (!"ROLE_EMP".equals(role) && !"ROLE_MGR".equals(role)) {
            throw new AccessDeniedException("Chỉ bệnh nhân và bác sĩ mới được xóa item khỏi giỏ hàng");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item trong giỏ hàng"));

        if (!cartItem.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Không có quyền xóa item này");
        }

        cartItemRepository.delete(cartItem);
    }

    public CartItemDTO updateQuantity(String username, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item trong giỏ hàng"));

        if (!cartItem.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Không có quyền cập nhật item này");
        }

        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);
        return convertToDTO(cartItem);
    }


    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setMedicineId(cartItem.getMedicine().getMedicineId());
        dto.setMedicineName(cartItem.getMedicine().getName());
        dto.setUnitPrice(cartItem.getMedicine().getUnitPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice(cartItem.getMedicine().getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return dto;
    }

    @Transactional
    public CheckoutResponseData checkout(String username, String role) throws Exception {
        if (!"ROLE_EMP".equals(role) && !"ROLE_MGR".equals(role)) {
            throw new AccessDeniedException("Chỉ bệnh nhân và bác sĩ mới được thanh toán");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));


        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán");
        }

        // Kiểm tra số lượng thuốc còn lại
        for (CartItem item : cartItems) {
            Medicine medicine = item.getMedicine();
            if (medicine.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Thuốc " + medicine.getName() + " không đủ số lượng trong kho");
            }
        }

        // Tính tổng số lượng sản phẩm trong đơn hàng
        int totalQuantity = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        // Tạo đơn hàng với PayOS
        String currentTimeString = String.valueOf(Instant.now().getEpochSecond());
        Integer payosOrderCode = Integer.valueOf(currentTimeString.substring(currentTimeString.length() - 6));

        String description = "Payment for order #" + payosOrderCode;
        String returnUrl = "https://xxxxxxxxxx.ngrok-free.app/payment/success"; //! CHANGE THIS TO YOUR NGROK GATEWAY PORT
        String cancelUrl = "https://xxxxxxxxxx.ngrok-free.app/payment/cancel"; //! CHANGE THIS TO YOUR NGROK GATEWAY PORT

        // Tính tổng số tiền cần thanh toán
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getMedicine().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lưu đơn hàng và thông tin thanh toán vào cơ sở dữ liệu
        Order order = new Order();
        order.setPayosOrderCode(String.valueOf(payosOrderCode));
        order.setUser(user);
        order.setDoctor(userRepository.findById(2L).orElse(null));
        order.setOrderDate(Instant.now());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(user);
        payment.setPaymentStatus("PENDING");
        payment.setPaymentMethod("Online");
        payment.setTransactionId(String.valueOf(payosOrderCode));
        payment.setPaymentDate(Instant.now());
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);

        // Create PayOS Payment link
        ItemData item = ItemData.builder()
                .name("Đơn hàng cho " + user.getName())
                .quantity(totalQuantity)
                .price(totalAmount.intValue())
                .build();
        PaymentData paymentData = PaymentData.builder()
                .orderCode(Long.valueOf(payosOrderCode))
                .amount(totalAmount.intValue())
                .description(description)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item)
                .build();

        // Cập nhật số lượng thuốc và xóa giỏ hàng
        for (CartItem items : cartItems) {
            Medicine medicine = items.getMedicine();
            medicine.setQuantity(medicine.getQuantity() - item.getQuantity());
            medicineRepository.save(medicine);
        }

        // Xóa toàn bộ giỏ hàng
        cartItemRepository.deleteByUser(user);

        try {
            return payOS.createPaymentLink(paymentData);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create PayOS payment link: " + e.getMessage());
        }
    }
}

