package com.zia.payment.service.repository;

import com.zia.payment.service.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<TransactionDetail, Long> {
}
