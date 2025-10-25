package com.zia.payment.service.controller;

import com.zia.payment.service.model.PaymentRequest;
import com.zia.payment.service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        Long transactionId = paymentService.doPayment(paymentRequest);
        return ResponseEntity.ok(transactionId);
    }

}
