package com.tt.talktok.controller;

import com.tt.talktok.dto.LectureDto;
import com.tt.talktok.dto.ReviewDto;
import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.dto.TeacherDto;
import com.tt.talktok.entity.Review;
import com.tt.talktok.service.LectureService;
import com.tt.talktok.service.ReviewService;
import com.tt.talktok.service.StudentService;
import com.tt.talktok.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("review")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final StudentService studentService;
    private final LectureService lectureService;
    private final TeacherService teacherService;

    @GetMapping("/list")
    public String reviewAllFind(Model model, @PageableDefault(page = 0, size = 10, sort = "revNo", direction = Sort.Direction.DESC) Pageable pageable
                                            ,@RequestParam(required = false, name = "search_target") String search_target,@RequestParam(required = false, name = "keyword") String keyword) {

        Page<ReviewDto>reviews = reviewService.reviewFindAll(search_target, keyword, pageable);


        log.info("review: {}", reviews.getContent());

        model.addAttribute("keyword", keyword);
        model.addAttribute("search_target", search_target);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);
        return "review/list";
    }

    @GetMapping("/detail")
    public String reviewDetail(Model model, int rev_no) {
        reviewService.reviewCountUpdate(rev_no);
        ReviewDto review = reviewService.reviewFindDetail(rev_no);

        log.info("review: {}", review);
        model.addAttribute("review", review);
        return "review/detail";
    }

    @GetMapping("/write")
    public String writeForm(@RequestParam(name = "lec_no") int lec_no, @RequestParam(name = "tea_no") int tea_no, Model model, HttpSession session){
        StudentDto studentDto = studentService.findStudent((String)session.getAttribute("stuEmail"));
        System.out.println(studentDto.getStuNo());

        LectureDto lectureDto = lectureService.findLectureByLecNo(lec_no);
        TeacherDto teacherDto = teacherService.findTeacher(tea_no);

        model.addAttribute("teacher", teacherDto);
        model.addAttribute("lecture", lectureDto);
        model.addAttribute("student", studentDto);
        model.addAttribute("lec_no", lec_no);
        model.addAttribute("tea_no", tea_no);

        return "review/writeForm";
    }
    @PostMapping("/write")
    public String write(ReviewDto reviewDto){
        log.info("reviewDto: {}", reviewDto);
        reviewService.reviewWrite(reviewDto);
        return "redirect:/student/lecture";
    }

    @GetMapping("/able")
    public String able(Model model, @PageableDefault(size = 10, sort = "revDate", direction = Sort.Direction.DESC) Pageable pageable,
                       @RequestParam int stu_no) {

        Page<ReviewDto> reviews = reviewService.reviewFindAble(stu_no, pageable);
        model.addAttribute("stuNo", stu_no);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);
        return "review/able";
    }

    @GetMapping("/update")
    public String updateForm(int rev_no, int stu_no, Model model){
        ReviewDto review = reviewService.reviewFindUpdateDetail(rev_no);

        model.addAttribute("rev_no", rev_no);
        model.addAttribute("review", review);
        return "review/updateForm";
    }
    @PostMapping("/update")
    public String update(ReviewDto reviewDto, int stu_no){
        System.out.println("업데이트 진입");
        System.out.println(reviewDto);
        reviewService.reviewUpdate(reviewDto);
        return "redirect:/review/able?stu_no=" + stu_no;
    }
    @GetMapping("/delete")
    public String delete(@RequestParam(name = "rev_no") int rev_no, int stu_no){
        reviewService.reviewDelete(rev_no);
        return "redirect:/review/able?stu_no=" + stu_no;
    }
    @GetMapping("/mylist")
    public String myReview(HttpServletRequest request, Model model, @PageableDefault(size = 10, sort = "revNo", direction = Sort.Direction.DESC) Pageable pageable) {
        HttpSession session = request.getSession();
        int stuNo = (int) session.getAttribute("stuNo"); // 세션에서 사용자 번호 가져오기
        System.out.println(stuNo);
        List<ReviewDto> stuReview = reviewService.findReviewsByStudentNo(stuNo);
        Page<ReviewDto> reviews = reviewService.reviewFindAll(pageable);
        log.info("review: {}", reviews.getContent());

        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);
        model.addAttribute("stuReviews", stuReview);
        model.addAttribute("stuNo", stuNo); // 모델에 사용자 번호 추가

        System.out.println(stuNo);
        System.out.println(session.getAttribute("stuNo"));
        return "review/myReview";
    }


}
