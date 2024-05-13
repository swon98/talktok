package com.tt.talktok.controller;

import com.tt.talktok.dto.LectureDto;
import com.tt.talktok.dto.NoticeDto;
import com.tt.talktok.dto.ReviewDto;
import com.tt.talktok.dto.TeacherDto;
import com.tt.talktok.entity.Lecture;
import com.tt.talktok.service.LectureService;
import com.tt.talktok.service.NoticeService;
import com.tt.talktok.service.ReviewService;
import com.tt.talktok.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final LectureService lectureService;
    private final TeacherService teacherService;
    private final ReviewService reviewService;
    private final NoticeService noticeService;

    @GetMapping({"/", "/main"})
    public String mainRequest(Model model, @PageableDefault(page = 0, size = 10, sort = "revReadCount", direction = Sort.Direction.DESC) Pageable pageable) {
        // 후기가 제일 많은 강의 3개 번호 불러오기
        List<Integer> lecNoList = reviewService.findBestLecNo();
        System.out.println("best lecture no :" + lecNoList);
        // 강의 번호로 강의 3개 정보 불러오기
        List<LectureDto> lectureList = new ArrayList<LectureDto>();
        for (Integer lecNo : lecNoList) {
            LectureDto lectureDto = lectureService.findLectureByLecNo(lecNo);
            //System.out.println(lectureDto);
            lectureList.add(lectureDto);
        }

        // 후기가 제일 많은 선생님 3개 번호 불러오기
        List<Integer> teaNoList = reviewService.findBestTeaNo();
        System.out.println("best teacher no : "+ teaNoList);
        // 선생님 번호로 선생님 3명 정보 불러오기
        List<TeacherDto> teacherList = new ArrayList<TeacherDto>();
        for (Integer teaNo : teaNoList) {
            TeacherDto teacherDto = teacherService.findTeacher(teaNo);
            //System.out.println(teacherDto);
            teacherList.add(teacherDto);
        }

        // 후기 조회수 순으로 10개 불러오기
        Page<ReviewDto> reviewList = reviewService.reviewFindAll(pageable);
        
        // 공지사항 최신 순으로 10개 불러오기
        List<NoticeDto> noticeList = noticeService.findAllMain();

        model.addAttribute("lectureList",lectureList);
        model.addAttribute("teacherList",teacherList);
        model.addAttribute("reviewList",reviewList);
        model.addAttribute("noticeList",noticeList);

        return "main";
    }
}
