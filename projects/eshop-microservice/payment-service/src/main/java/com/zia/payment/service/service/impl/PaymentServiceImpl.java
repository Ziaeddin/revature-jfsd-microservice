package com.zia.payment.service.service.impl;

import com.zia.payment.service.entity.TransactionDetail;
import com.zia.payment.service.model.PaymentRequest;
import com.zia.payment.service.repository.PaymentRepository;
import com.zia.payment.service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        TransactionDetail transactionDetail = new TransactionDetail();
        transactionDetail.setOrderId(paymentRequest.getOrderId());
        transactionDetail.setAmount(paymentRequest.getAmount());
        transactionDetail.setPaymentDate(Instant.now());
        transactionDetail.setPaymentMethod(paymentRequest.getPaymentMethod().name());
        transactionDetail.setReferenceNumber(paymentRequest.getReferenceNumber());
        transactionDetail.setPaymentStatus("SUCCESS");

        transactionDetail = paymentRepository.save(transactionDetail);
        return transactionDetail.getId();
    }
}
