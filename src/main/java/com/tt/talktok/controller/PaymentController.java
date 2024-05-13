package com.tt.talktok.controller;

import com.tt.talktok.dto.PaymentDto;
import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.entity.Payment;
import com.tt.talktok.service.CartService;
import com.tt.talktok.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final CartService cartService;

    //단건 결제
    @PostMapping("/savePayment")
    @ResponseBody
    public ResponseEntity<String> savePayment(@RequestBody Map<String, Object> requestData) {

        System.out.println("requestData="+requestData.toString()); // 값 전달 되었는지 확인

        Map<String, Object> paymentInfo = (Map<String, Object>) requestData.get("paymentInfo");
        System.out.println("Price: " + paymentInfo.get("price"));
        System.out.println("Lecture Name: " + paymentInfo.get("lecName"));
        System.out.println("Email: " + paymentInfo.get("email"));
        System.out.println("Lecture No: " + paymentInfo.get("lecNo"));

        PaymentDto paymentDto = new PaymentDto();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        paymentDto.setPay_time(currentTime);
        paymentDto.setPay_price(String.valueOf(paymentInfo.get("price")));
        paymentDto.setLec_name((String) paymentInfo.get("lecName"));
        paymentDto.setStu_Email((String) paymentInfo.get("email"));
        paymentDto.setLec_no(((int) paymentInfo.get("lecNo")));

        paymentDto = paymentService.save(paymentDto);
        if(paymentDto != null) {
            return ResponseEntity.ok("Payment 저장 완료");
        } else {
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
    }
    
    // 다중 결제, 결제 건 장바구니 삭제
    @PostMapping("/savePayments")
    @ResponseBody
    public ResponseEntity<String> savePayments(@RequestBody Map<String, List<Map<String, Object>>> requestData,
                                               HttpSession session) {

        System.out.println("requestData="+requestData.toString()); // 값 전달 되었는지 확인

        List<Map<String, Object>> paymentInfoList = requestData.get("paymentInfoList");
        for (Map<String, Object> paymentInfo : paymentInfoList) {
            System.out.println("Price: " + paymentInfo.get("price"));
            System.out.println("Lecture Name: " + paymentInfo.get("lecName"));
            System.out.println("Email: " + paymentInfo.get("email"));
            System.out.println("Lecture No: " + paymentInfo.get("lecNo"));

            PaymentDto paymentDto = new PaymentDto();
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            paymentDto.setPay_time(currentTime);
            paymentDto.setPay_price(String.valueOf(paymentInfo.get("price")));
            paymentDto.setLec_name((String) paymentInfo.get("lecName"));
            paymentDto.setStu_Email((String) paymentInfo.get("email"));
            paymentDto.setLec_no((int) paymentInfo.get("lecNo"));

            paymentDto = paymentService.save(paymentDto);
            if (paymentDto != null) {
                System.out.println("Payment 저장 완료");
                int stuNo = (int) session.getAttribute("stuNo");
                cartService.checkCart(stuNo, paymentDto.getLec_no());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok("모든 Payment 저장 완료");
    }
}
