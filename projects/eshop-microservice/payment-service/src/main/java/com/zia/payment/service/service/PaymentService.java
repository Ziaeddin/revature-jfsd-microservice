package com.zia.payment.service.service;


import com.zia.payment.service.model.PaymentRequest;

public interface PaymentService {
        Long doPayment(PaymentRequest paymentRequest);
    }

