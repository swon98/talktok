package com.tt.talktok.controller;

import com.tt.talktok.dto.*;
import com.tt.talktok.service.LectureService;
import com.tt.talktok.service.ReviewService;
import com.tt.talktok.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lecture")
public class LectureController {

    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final TeacherService teacherService;

    @GetMapping("/list")
    public String list(@PageableDefault(page = 0, size = 8, sort = "lecNo", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        Page<LectureDto> lectureList = lectureService.findAll(pageable);
        int currentPage = lectureList.getNumber(); // 현재 페이지 번호 가져가기(1번부터 시작하기)
        model.addAttribute("lectureList",lectureList);
        model.addAttribute("currentPage",currentPage);
        return "lecture/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("no") int lec_no, @RequestParam("page") int currentPage,
                         Model model, HttpSession session) {
        // 강의 목록
        LectureDto lectureDto = lectureService.findLectureByLecNo(lec_no);
        model.addAttribute("lectureDto",lectureDto);
        
        // 리뷰
        int tea_no = lectureDto.getTea_no();
        List<ReviewDto> reviews = reviewService.reviewFindTeacher(tea_no);
        model.addAttribute("reviews", reviews);


        // 유저정보
        String email = (String) session.getAttribute("stuEmail");
        System.out.println(email);
        model.addAttribute("email", email);

        int page = currentPage;
        model.addAttribute("page",page);

        return "lecture/detail";
    }


}
