package com.tt.talktok.controller;

import com.tt.talktok.dto.PaymentDto;
import com.tt.talktok.dto.ReviewDto;
import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.entity.Lecture;
import com.tt.talktok.repository.LectureRepository;
import com.tt.talktok.service.PaymentService;
import com.tt.talktok.service.ReviewService;
import com.tt.talktok.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final LectureRepository lectureRepository;
    @Value("${spring.mail.hostSMTPid}")
    String hostSMTPid;

    @Value("${spring.mail.hostSMTPpwd}")
    String hostSMTPpwd;

    private final BCryptPasswordEncoder passwordEncoder;
    private final StudentService studentService;

    // 학생 로그인
    @GetMapping("/login")
    public String login() {
        return "student/loginForm";    }
    @PostMapping("/login")
    public String login(@ModelAttribute StudentDto student, Model model, HttpSession session) {
        int result = 0;
        System.out.println(student.getStuPwd());
        String email = student.getStuEmail();
        StudentDto dbStudent = studentService.findStudent(email);

        // 등록되지 않은 학생의 경우
        if(dbStudent.getStuEmail() == null){

            System.out.println("등록되지 않은 학생의 경우");
            model.addAttribute("result", result);
            // 등록된 학생의 경우
        } else {
            //비번이 같을때
            if(passwordEncoder.matches(student.getStuPwd(), dbStudent.getStuPwd())){
                result = 1;

                System.out.println("비번이 같을때");

                session.setAttribute("stuEmail", email);
                session.setAttribute("stuNo", dbStudent.getStuNo());
                session.setAttribute("stuSocial", dbStudent.getStuSocial());
                session.setAttribute("studentDto", dbStudent); //장바구니용 테스트

                model.addAttribute("result", result);
            //비번이 다를때
            } else {
                System.out.println("비번이 다를때");
                result = 2;
                model.addAttribute("result", result);
            }
        }
        return "student/login";
    }

    // 학생 회원가입
    @GetMapping("/join")
    public String join() {
        return "student/joinForm";    }
    @PostMapping("/join")
    public String join(@ModelAttribute StudentDto student, Model model) {
        int result = 0;

        String stuEmail = student.getStuEmail();

        StudentDto dbStudent = studentService.findStudent(stuEmail);
        //가입된 email = 1, 가입안된 email = 0
        studentService.join(student);
        model.addAttribute("result",result);

        return "student/join";
    }


    // 학생 아이디 중복검사(ajax 리턴)
    @PostMapping("/idCheck")
    @ResponseBody
    public int idcheck(@RequestParam("stuEmail") String stuEmail) {
        int result = 0;

        StudentDto student = studentService.findStudent(stuEmail);
        if (student.getStuEmail() != null) { // 중복 ID
            result = 1;
        } else { // 사용가능 ID
            result = -1;
        }

        return result;
    }

    // 학생 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "student/logout";
    }

    // 학생 마이페이지
    @GetMapping("/myPage")
    public String myPage() {
        return "student/myPage";
    }

    // 학생 비밀번호 찾기
    @GetMapping("/findPwd")
    public String findPwd(){
        return "student/findPwdForm";
    }
    @PostMapping("/findPwd")
    public String findPwd(@ModelAttribute StudentDto studentDto, Model model) {
        int result = 0;

        String stuEmail = studentDto.getStuEmail();
        StudentDto dbStudent = studentService.findStudent(stuEmail);
        if (dbStudent.getStuEmail() == null) {
            model.addAttribute("result",result);
            return "stduent/findPwd";

        } else {
            // 비밀번호를 업데이트 할 dto 객체를 생성
            StudentDto student = new StudentDto();
            //비밀번호를 난수화하여 db 에 저장
            Random random = new Random();
            String newPwd = String.valueOf(random.nextInt(999999));
            student.setStuEmail(stuEmail);
            student.setStuPwd(passwordEncoder.encode(newPwd));
            studentService.updatePwd(student);

            // Mail Server 설정
            String charSet = "utf-8";
            String hostSMTP = "smtp.naver.com";


            // 보내는 사람 EMail, 제목, 내용
            String fromEmail = "aircamp03@naver.com";
            String fromName = "관리자";
            String subject = "비밀번호 찾기";

            // 받는 사람 E-Mail 주소 : 원래는 받는 사람도 직접 작성했지만, 여기서는 DB에 저장된 정보가 존재 해서 그걸 불러서 사용한다.

            try {
                HtmlEmail email = new HtmlEmail();
                email.setDebug(true);
                email.setCharset(charSet);
                email.setSSL(true);
                email.setHostName(hostSMTP);
                email.setSmtpPort(587);

                email.setAuthentication(hostSMTPid, hostSMTPpwd);
                email.setTLS(true);
                email.addTo(stuEmail, charSet);
                email.setFrom(fromEmail, fromName, charSet);
                email.setSubject(subject);
                email.setHtmlMsg("<p align = 'center'>비밀번호 찾기</p><br>" +
                        "<div align='center'> 임시 비밀번호:"+newPwd+"</div>");
                email.send();
            } catch (Exception e) {
                System.out.println(e);
            }

            result = 1;
            model.addAttribute("result",result);

            return "student/findPwd";

        }
    }

    // 학생 회원 탈퇴 양식으로 이동
    @GetMapping("/withdraw")
    public String withdraw() {
        return "student/withdrawForm";
    }

    // 학생 회원탈퇴
    @PostMapping("/withdraw")
    public String withdraw(@ModelAttribute StudentDto studentDto, HttpSession session, Model model) {
        int result=0;

        String stuEmail = (String) session.getAttribute("stuEmail");

        StudentDto dbStudent = studentService.findStudent(stuEmail);

        String rawPwd = studentDto.getStuPwd();

        //비밀번호 일치시 회원탈퇴.
        if (passwordEncoder.matches(rawPwd, dbStudent.getStuPwd())) {
            System.out.println("stuEmail" +stuEmail);
            studentService.withdraw(stuEmail);
            session.invalidate();

            result=1;
            model.addAttribute("result",result);
            return "student/withdraw";
            //비밀번호 불일치시
        } else {
            model.addAttribute("result",result);
            return "student/withdraw";
        }
    }

    // 학생 비밀번호 변경
    @GetMapping("pwdUpdate")
    public String pwdUpdate() {
        return "student/pwdUpdateForm";
    }
    @PostMapping("pwdUpdate")
    public String pwdUpdate(StudentDto studentDto, @RequestParam("stuNewPwd") String stuNewPwd, Model model, HttpSession session) {
        String stuEmail = (String) session.getAttribute("stuEmail");
        StudentDto dbStudent = studentService.findStudent(stuEmail);
        int result = 0;
        System.out.println("Password check before: " + passwordEncoder.matches(studentDto.getStuPwd(), dbStudent.getStuPwd()));

        // 미리 newStudent 객체 생성
        StudentDto newStudent = new StudentDto();
        newStudent.setStuEmail(stuEmail);  // 이메일 설정도 세션에서 가져온 이메일로 변경

        // 현재 비밀번호 확인
        if (passwordEncoder.matches(studentDto.getStuPwd(), dbStudent.getStuPwd())) {
            String encpassword = passwordEncoder.encode(stuNewPwd);
            newStudent.setStuPwd(encpassword);
            studentService.updatePwd(newStudent);
            result = 1;
        } else {
            result = -1;
        }

        System.out.println("Password check after: " + passwordEncoder.matches(studentDto.getStuPwd(), dbStudent.getStuPwd()));
        System.out.println(result);
        model.addAttribute("result", result);
        return "student/pwdUpdate";
    }

    // 학생 회원정보 수정
    @GetMapping("/update")
    public String update(HttpSession session, Model model) {
        String stuEmail = (String) session.getAttribute("stuEmail");
        StudentDto studentDto = studentService.findStudent(stuEmail);
        if(studentDto.getStuSocial().equals("normal")) {
            model.addAttribute("studentDto", studentDto);
            return "student/updateForm";
        }else {
            model.addAttribute("studentDto", studentDto);
            return "student/updateSocial";
        }
    }
    @PostMapping("/update")
    public String update(@ModelAttribute StudentDto studentDto, HttpSession session, Model model) throws Exception {
        String stuEmail = (String) session.getAttribute("stuEmail");
        studentDto.setStuEmail(stuEmail);
        // 최대한 빨리 stuSocial의 null 체크를 수행
        String stuSocial = Optional.ofNullable(studentDto.getStuSocial()).orElse("normal");
        studentDto.setStuSocial(stuSocial);  // 확실하게 stuSocial 값을 설정

        StudentDto dbStudent = studentService.findStudent(stuEmail);
        // student update
        if (stuSocial.equals("normal")) {
            if (passwordEncoder.matches(studentDto.getStuPwd(), dbStudent.getStuPwd())) {
                // 비밀번호 일치: 회원 정보 업데이트
                studentService.update(studentDto);
                System.out.println("정보 수정 완료");
                return "redirect:/student/myPage"; // 정보 업데이트 후 마이페이지로 리다이렉트
            } else {// 비밀번호 불일치
                return "student/update";
            }
        } else {
            studentService.update(studentDto);
            return "redirect:/student/myPage";
        }
    }

    // 학생 결제내역
    @GetMapping("/payment")
    public String myPayment(HttpServletRequest request, Model model, @PageableDefault(size = 10, sort = "revNo", direction = Sort.Direction.DESC) Pageable pageable) {
        HttpSession session = request.getSession();
        String stuEmail = (String) session.getAttribute("stuEmail"); // 세션에서 사용자 번호 가져오기
        System.out.println(stuEmail);
        List<PaymentDto> stuPayment = paymentService.findPaymentByStudentEmail(stuEmail);
        Page<ReviewDto> reviews = reviewService.reviewFindAll(pageable);
//        log.info("review: {}", reviews.getContent());

        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);
        model.addAttribute("stuPayment", stuPayment);
        model.addAttribute("stuEmail", stuEmail); // 모델에 사용자 번호 추가

        System.out.println("세션 이메일: " + session.getAttribute("stuEmail"));
        System.out.println("결제 내역 확인 : " + stuPayment);
        return "student/payment";
    }

    // 학생 결제한 강의 내역
    @GetMapping("/lecture")
    public String myLecture(HttpServletRequest request, Model model, @PageableDefault(size = 10, sort = "revNo", direction = Sort.Direction.DESC) Pageable pageable) {
        HttpSession session = request.getSession();
        String stuEmail = (String) session.getAttribute("stuEmail"); // 세션에서 사용자 번호 가져오기
        Page<ReviewDto> reviews = reviewService.reviewFindAll(pageable);

        StudentDto studentDto = studentService.findStudent(stuEmail);

        List<PaymentDto> lecture = paymentService.findPaymentByStudentEmail(stuEmail);
        Map<Integer, Lecture> stuLecture = paymentService.findLecturesForPayments(lecture);
        Set<Integer> keySet = stuLecture.keySet();
        List<Integer> keyList = new ArrayList<>(keySet);
        List<Integer> reviewCheck = new ArrayList<>();

        for(int i = 0;i<stuLecture.size();i++) {
            int result = reviewService.reviewCheck(studentDto.getStuNo(),keyList.get(i));
            System.out.println("stuNo"+studentDto.getStuNo());
            System.out.println("lecNo"+lecture.get(i).getLec_no());
            reviewCheck.add(result);
            System.out.println(lecture.get(i).getLec_no());
            System.out.println(stuLecture.get(lecture.get(i).getLec_no()));
        }


        System.out.println("reviewCheck"+reviewCheck.toString());

        model.addAttribute("studentDto", studentDto);
        model.addAttribute("reviewCheck", reviewCheck);

        model.addAttribute("stuLecture", stuLecture);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("page", reviews);

        System.out.println("세션 이메일: " + session.getAttribute("stuEmail"));
        System.out.println("강의 내역 확인 : " + stuLecture);
        return "student/lecture";
    }
}