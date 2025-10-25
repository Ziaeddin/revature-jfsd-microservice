package com.zia.payment.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "transaction_details"
)
public class TransactionDetail {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_mode", nullable = false)
    private String paymentMethod;

    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Column(name = "payment_date", nullable = false)
    private Instant paymentDate;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "payment_amount", nullable = false)
    private Double amount;
}
