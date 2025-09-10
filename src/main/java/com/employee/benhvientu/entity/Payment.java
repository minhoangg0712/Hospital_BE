package com.employee.benhvientu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @ColumnDefault("'Pending'")
    @Lob
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payos_order_code", length = 100)
    private String payosOrderCode;

}