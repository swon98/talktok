package com.tt.talktok.service;

import com.tt.talktok.dto.LectureDto;
import com.tt.talktok.dto.PaymentDto;
import com.tt.talktok.entity.Lecture;
import com.tt.talktok.entity.Payment;
import com.tt.talktok.repository.LectureRepository;
import com.tt.talktok.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.sql.Timestamp;
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LectureRepository lectureRepository;

    private Payment convertToEntity(PaymentDto paymentDto) {
        Payment payment = new Payment();
        payment.setStuEmail(paymentDto.getStu_Email());
        payment.setPay_price(paymentDto.getPay_price());
        payment.setLec_name(paymentDto.getLec_name());
        payment.setLecNo(paymentDto.getLec_no());
        payment.setPayTime(new Timestamp(System.currentTimeMillis())); // 현재 시간으로 설정
        return payment;
    }

    private PaymentDto convertToDto(Payment payment) { // Payment 엔티티를 받아서
        PaymentDto dto = new PaymentDto(); //새로운 PaymentDto 객체 생성
        dto.setStu_Email(payment.getStuEmail()); // 엔티티의 각 필드 값을 DTO 의 필드에 복사
        dto.setPay_price(payment.getPay_price());
        dto.setLec_name(payment.getLec_name());
        dto.setLec_no(payment.getLecNo());
        dto.setPay_time(payment.getPayTime());
        return dto; // 변환된 Dto 객체를 반환
    }

    // 결제 정보 저장
    public PaymentDto save(PaymentDto paymentDto) {
        Payment payment = convertToEntity(paymentDto);
        payment = paymentRepository.save(payment); // 엔티티를 저장하고 저장된 엔티티를 반환
        return convertToDto(payment);
    }



    // 강의 결제 내역
    public List<PaymentDto> findPaymentByStudentEmail(String stuEmail) {
        List<Payment> payment = paymentRepository.findByStuEmail(stuEmail);
        return payment.stream().map(this::convertToDto).collect(Collectors.toList());
    }


    // 결제한 강의 목록
    public Map<Integer, Lecture> findLecturesForPayments(List<PaymentDto> payments) {
        List<Integer> lecIds = payments.stream()
                .map(PaymentDto::getLec_no) // PaymentDto에서 강의 번호 추출
                .collect(Collectors.toList()); // 중복 제거
        List<Lecture> lectures = lectureRepository.findAllById(lecIds);
        for(int i = 0; i<lectures.size(); i++) {
        System.out.println("service:"+lectures.get(i).getLecNo());

        }
        return lectures.stream()
                .collect(Collectors.toMap(Lecture::getLecNo, Function.identity()));
    }


}
