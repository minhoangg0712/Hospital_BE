package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.CartItemDTO;
import com.employee.benhvientu.dto.CartResponseDTO;
import com.employee.benhvientu.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_EMP', 'ROLE_MGR')")
    public ResponseEntity<CartItemDTO> addToCart(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Không có quyền truy cập"))
                .getAuthority();

        return ResponseEntity.ok(cartService.addToCart(username, medicineId, quantity, role));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMP', 'ROLE_MGR')")
    public ResponseEntity<CartResponseDTO> getCartItems(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Không có quyền truy cập"))
                .getAuthority();

        return ResponseEntity.ok(cartService.getCartWithSummary(username, role));
    }

    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMP', 'ROLE_MGR')")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Không có quyền truy cập"))
                .getAuthority();

        cartService.removeFromCart(username, cartItemId, role);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.updateQuantity(username, cartItemId, quantity));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('ROLE_EMP', 'ROLE_MGR')")
    public ResponseEntity<Map<String, Object>> checkout(Authentication authentication) throws Exception {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Không có quyền truy cập"))
                .getAuthority();

        CheckoutResponseData checkoutData = cartService.checkout(username, role);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("orderId", checkoutData.getOrderCode());
        responseData.put("checkoutUrl", checkoutData.getCheckoutUrl());
        responseData.put("paymentLinkId", checkoutData.getPaymentLinkId());
        return ResponseEntity.ok(responseData);

    }
}
