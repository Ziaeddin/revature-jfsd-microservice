package com.zia.payment.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private Double amount;
    private String referenceNumber;
    private PaymentMethod paymentMethod;
}
