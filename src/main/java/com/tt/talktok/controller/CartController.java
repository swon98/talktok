package com.tt.talktok.controller;


import com.tt.talktok.dto.CartDto;
import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.entity.Cart;
import com.tt.talktok.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 추가
    @PostMapping("/add")
    public String cart(@RequestParam("lec_no") int lec_no, @ModelAttribute CartDto cartDto, HttpSession session, Model model) {
//        StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");
        String stuEmail = (String) session.getAttribute("stuEmail");

        // 받아온 stuNo 값을 Integer 에서 int 로 변환, Integer null 일시 0 할당
        Integer stuNoInteger = (Integer) session.getAttribute("stuNo");
        int stuNo = (stuNoInteger != null) ? stuNoInteger.intValue() : 0;

        System.out.println(stuEmail);

        if(stuEmail == null){
            return "cart/loginmessage";
        } else {

            // 장바구니에 기존 상품이 있는지 검사
            int count = cartService.countCart(lec_no, stuNo);

            if(count == 0){
                System.out.println("stuNo : "+stuNo);
                System.out.println("lecNO : "+lec_no);

                cartService.addCart(stuNo, lec_no);

                System.out.println("장바구니 추가");

                return "cart/smessage";

            } else{
                return "cart/fmessage";
            }
        }
    }

    // 장바구니 목록 조회
    @GetMapping("/list")
    public String cartList(HttpSession session, Model model, @RequestParam(defaultValue = "1") int page) {
        StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");

        String stuEmail = (String) session.getAttribute("stuEmail");
        int stuNo = (int) session.getAttribute("stuNo");

//        if(studentDto == null){
        if(stuEmail == null){
            return "redirect:/student/login";
        }else{
//            List<Cart> cartItems = cartService.getCartItems(studentDto.getStuNo());
            List<Cart> cartItems = cartService.getCartItems(stuNo);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("page", page);
        }
        return "cart/list";
    }

    // 장바구니에서 선택항목 삭제
    @PostMapping("/delete")
    public String deleteCart(@RequestParam(value = "cartCheck", required = false) List<String> cartCheck, HttpSession session) {
        //StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");
        String stuEmail = (String) session.getAttribute("stuEmail");

        //if (studentDto == null) {
        if (stuEmail == null) {
            return "redirect:/student/login";
        }else if(cartCheck == null || cartCheck.isEmpty()){
            return "cart/cbmessage";
        } else {
            for (String item : cartCheck) {
                String[] parts = item.split("-");
                int stuNo = Integer.parseInt(parts[0]);
                int lecNo = Integer.parseInt(parts[1]);

                System.out.println("서비스 가기 전");

                cartService.checkCart(stuNo, lecNo);
            }

            System.out.println("끝");

            return "redirect:/cart/list";
        }
    }
}
