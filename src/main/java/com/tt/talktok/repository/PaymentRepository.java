package com.tt.talktok.repository;

import com.tt.talktok.dto.PaymentDto;
import com.tt.talktok.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // 결제 정보 저장
    //public int savePayment(PaymentDto paymentDto);
    public Payment save(Payment payment);

    List<Payment> findByStuEmail(String stuEmail);

}
