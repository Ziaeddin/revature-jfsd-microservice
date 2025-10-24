package com.zia.order.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long productId;
    private Long quantity;
    private Double amount;
    private PaymentMethod paymentMethod;
}
