package com.employee.benhvientu.dto;
import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {
    private List<CartItemDTO> items;
    private CartSummary summary;

    public CartResponseDTO(List<CartItemDTO> items) {
        this.items = items;
        calculateSummary();
    }

    private void calculateSummary() {
        BigDecimal subtotal = items.stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = new BigDecimal("30000.00");
        BigDecimal total = subtotal.add(shippingFee);

        this.summary = new CartSummary(shippingFee, subtotal, total);
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public CartSummary getSummary() {
        return summary;
    }

    public void setSummary(CartSummary summary) {
        this.summary = summary;
    }

    public static class CartSummary {
        private BigDecimal shippingFee;
        private BigDecimal subtotal;
        private BigDecimal total;

        public CartSummary(BigDecimal shippingFee, BigDecimal subtotal, BigDecimal total) {
            this.shippingFee = shippingFee;
            this.subtotal = subtotal;
            this.total = total;
        }

        public BigDecimal getShippingFee() {
            return shippingFee;
        }

        public void setShippingFee(BigDecimal shippingFee) {
            this.shippingFee = shippingFee;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }
    }
}