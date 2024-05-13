package com.tt.talktok.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tt.talktok.dto.LectureDto;
import com.tt.talktok.dto.ReviewDto;
import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.dto.TeacherDto;
import com.tt.talktok.entity.Review;
import com.tt.talktok.entity.Teacher;
import com.tt.talktok.service.LectureService;
import com.tt.talktok.service.ReviewService;
import com.tt.talktok.service.TeacherService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {
    @Value("${spring.mail.hostSMTPid}")
    String hostSMTPid;
    @Value("${spring.mail.hostSMTPpwd}")
    String hostSMTPpwd;

    private final TeacherService teacherService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ReviewService reviewService;
    private final LectureService lectureService;

    // 강사 목록 조회
    @GetMapping("/list")
    public String list(
                        @PageableDefault(page=0, size = 12,direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(required = false, name = "searchTutorName") String keyword,
                        Model model) {
        Page<TeacherDto> list = null;
        if (keyword != null && !keyword.isEmpty()) {
            list = teacherService.teacherSearchList(keyword, pageable);
        }else{
            list = teacherService.teacherList(pageable);
        }
        System.out.println("keyword:" + keyword);
        model.addAttribute("teacherList", list);

        return "teacher/list";
    }

    //강사 목록 상세페이지
    @GetMapping("/detail")
    public String teacherDetail(@RequestParam("teaNo") int tea_no, Model model) {

        System.out.println("tea_no" + tea_no);
        //상세페이지
        TeacherDto teacherDetail = teacherService.getTeacherDetail(tea_no);
        //후기글
        List<ReviewDto> reviews = reviewService.reviewFindTeacher(tea_no);
        //강사 강의 조회
        List<LectureDto> lecture = lectureService.findAllByTeaNo(tea_no);

        model.addAttribute("lecture", lecture);
        model.addAttribute("reviews", reviews);
        model.addAttribute("teacherDetail", teacherDetail);

        return "teacher/detail";
    }
    //teacher 로그인
    @GetMapping("/login")
    public String login() {
        return "teacher/loginForm";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute TeacherDto teacher, Model model, HttpSession session) {
        int result = 0;
        System.out.println(teacher.getTeaPwd());
        String email = teacher.getTeaEmail();
        TeacherDto dbTeacher = teacherService.findTeacher(email);

        // 등록되지 않은 선생님의 경우 - dbTeacher로 null 비교를 할경우 entity에서 dto로 주소를 옮기며 주소값이 들어가 무조건 null이 아니게 됩니다.
        if(dbTeacher.getTeaEmail() == null){

            System.out.println("등록되지 않은 선생님의 경우");
            model.addAttribute("result", result);
            // 등록된 선생님의 경우
        } else {
            //비번이 같을때
            if(passwordEncoder.matches(teacher.getTeaPwd(), dbTeacher.getTeaPwd())){
                result = 1;

                System.out.println("비번이 같을때");
                session.setAttribute("teaEmail", email);
                session.setAttribute("teaNo",dbTeacher.getTeaNo());
                System.out.println("teaNo"+dbTeacher.getTeaNo());
                model.addAttribute("result", result);
                System.out.println("로그인 : "+ dbTeacher);
                //비번이 다를때
            } else {
                System.out.println("비번이 다를때");
                result = 2;
                model.addAttribute("result", result);
            }
        }
        return "teacher/login";
    }


    @GetMapping("/join")
    public String join() {
        return "teacher/joinForm";
    }


    //프로필 사진 경로
    @Value("${app.upload.dir}")
    private String uploadDir;

    //회원가입
    @PostMapping("/join")
    public String join(@RequestParam("teaImage1") MultipartFile mf,
                       @ModelAttribute TeacherDto teacher,
                       HttpSession  session,
                       Model model) throws Exception{
        System.out.println("taecher: "+teacher);

        String filename = mf.getOriginalFilename();
        int size = (int)mf.getSize();

        String path = Paths.get(uploadDir).toString();

        System.out.println("mf=" + mf);
        System.out.println("filename=" + filename); 	// filename="Koala.jpg"
        System.out.println("size=" + size);
        System.out.println("Path=" + path);


        int result = 0;
        String newfilename = "";
        String teaEmail = teacher.getTeaEmail();

        if(size > 0){ // 첨부파일이 전송된 경우
            // 파일 중복문제 해결
            String extension = filename.substring(filename.lastIndexOf("."), filename.length());
            System.out.println("extension:"+extension); // extension: .jpg
            UUID uuid = UUID.randomUUID();

            newfilename = uuid.toString() + extension;
            System.out.println("newfilename:"+newfilename);

            if(size > 100000){ // 100KB
                result=1;
                model.addAttribute("result", result);
                return "teacher/join";

            }else if(!extension.equals(".jpg")  &&
                    !extension.equals(".jpeg") &&
                    !extension.equals(".gif")  &&
                    !extension.equals(".png") ){
                result=2;
                model.addAttribute("result", result);
                return "teacher/join";
            }
        }
        if (size > 0) { // 첨부파일 전송
            mf.transferTo(new File(path + "/" + newfilename));
        }

        if(size>0){
            teacher.setTeaImage(newfilename);
            teacherService.join(teacher);
        }else{
            teacher.setTeaImage(null);
        }

        model.addAttribute("result",result);
        return "teacher/join";
    }

    // 아이디 중복검사(ajax 리턴)
    @PostMapping("/idCheck")
    @ResponseBody
    public int idcheck(@RequestParam("teaEmail") String teaEmail) {
        int result = 0;

        TeacherDto teacher = teacherService.findTeacher(teaEmail);
        if (teacher.getTeaEmail() != null) { // 중복 ID
            result = 1;
        } else { // 사용가능 ID
            result = -1;
        }

        return result;
    }


    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "teacher/logout";
    }

    //마이페이지
    @GetMapping("/myPage")
    public String myPage(HttpSession session, Model model) {
        int teaNo  = (int)session.getAttribute("teaNo");
        TeacherDto teacherDto = teacherService.getTeacherDetail(teaNo);

        model.addAttribute("teacher", teacherDto);
        return "teacher/myPage";
    }

    @GetMapping("/findPwd")
    public String findPwd(){
        return "teacher/findPwdForm";
    }


    @PostMapping("/findPwd")
    public String findPwd(@ModelAttribute TeacherDto teacherDto, Model model) {
        int result = 0;

        String teaEmail = teacherDto.getTeaEmail();
        TeacherDto dbTeacher = teacherService.findTeacher(teaEmail);
        if (dbTeacher.getTeaEmail() == null) {
            model.addAttribute("result",result);
            return "teacher/findPwd";

        } else {
            // 비밀번호를 업데이트 할 dto 객체를 생성
            TeacherDto teacher = new TeacherDto();
            //비밀번호를 난수화하여 db 에 저장
            Random random = new Random();
            String newPwd = String.valueOf(random.nextInt(999999));
            teacher.setTeaEmail(teaEmail);
            teacher.setTeaPwd(passwordEncoder.encode(newPwd));
            teacherService.updatePwd(teacher);

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
                email.addTo(teaEmail, charSet);
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

            return "teacher/findPwd";

        }
    }

    // 회원 탈퇴 양식으로 이동
    @GetMapping("/withdraw")
    public String withdraw() {
        return "teacher/withdrawForm";
    }


    // 회원탈퇴
    @PostMapping("/withdraw")
    public String withdraw(@ModelAttribute TeacherDto teacherDto, HttpSession session, Model model) {
        int result=0;

        String teaEmail = (String) session.getAttribute("teaEmail");

        TeacherDto dbTeacher = teacherService.findTeacher(teaEmail);

        String rawPwd = teacherDto.getTeaPwd();

        //비밀번호 일치시 회원탈퇴.
        if (passwordEncoder.matches(rawPwd, dbTeacher.getTeaPwd())) {
            teacherService.withdraw(teaEmail);
            session.invalidate();

            result=1;
            model.addAttribute("result",result);
            return "teacher/withdraw";
            //비밀번호 불일치시
        } else {
            model.addAttribute("result",result);
            return "teacher/withdraw";
        }

    }

    // 강사 비밀번호 변경
    @GetMapping("pwdUpdate")
    public String pwdUpdate() {
        return "teacher/pwdUpdateForm";
    }
    @PostMapping("pwdUpdate")
    public String pwdUpdate(TeacherDto teacherDto, @RequestParam("teaNewPwd") String teaNewPwd, Model model, HttpSession session) {
        String teaEmail = (String) session.getAttribute("teaEmail");

        int result = 0;
        TeacherDto dbTeacher = teacherService.findTeacher(teaEmail);
        System.out.println("Password check before: " + passwordEncoder.matches(teacherDto.getTeaPwd(), dbTeacher.getTeaPwd()));

        // 미리 newStudent 객체 생성
        TeacherDto newTeacher = new TeacherDto();
        newTeacher.setTeaEmail(teaEmail);  // 이메일 설정도 세션에서 가져온 이메일로 변경

        // 현재 비밀번호 확인
        if (passwordEncoder.matches(teacherDto.getTeaPwd(), dbTeacher.getTeaPwd())) {
            String encpassword = passwordEncoder.encode(teaNewPwd);
            newTeacher.setTeaPwd(encpassword);
            teacherService.updatePwd(newTeacher);
            result = 1;

        }else {
            result=-1;
        }
        System.out.println("Password check after: " + passwordEncoder.matches(teacherDto.getTeaPwd(), dbTeacher.getTeaPwd()));
        System.out.println(result);
        model.addAttribute("result", result);
        return "teacher/pwdUpdate";
    }

    // 강사 회원정보 수정
    @GetMapping("/update")
    public String update(HttpSession session, Model model) {
        String teaEmail = (String) session.getAttribute("teaEmail");
        TeacherDto teacherDto = teacherService.findTeacher(teaEmail);
        System.out.println("teacherDto.teaImage"+teacherDto.getTeaImage());
        model.addAttribute("teacherDto", teacherDto);
        return "teacher/updateForm";
    }
    @PostMapping("/update")
    public String update(@RequestParam("teaImage1")MultipartFile mf,@ModelAttribute TeacherDto teacherDto, HttpSession session, Model model) throws Exception {
        String teaEmail = (String) session.getAttribute("teaEmail");
        teacherDto.setTeaEmail(teaEmail);
        System.out.println("ControllerTeacherDto: " + teacherDto.toString());

        String filename = mf.getOriginalFilename();
        int size = (int)mf.getSize();

        String path = Paths.get(uploadDir).toString();

        TeacherDto dbTeacher = teacherService.findTeacher(teacherDto.getTeaEmail());

        String newfilename="";

        if(size>0){
            //파일 중복 문제
            String extension = filename.substring(filename.lastIndexOf("."),filename.length());

            if(size > 100000){ // 100KB

                model.addAttribute("result", 1);
                return "teacher/update";

            }

            if(!extension.equals(".jpg") &&
                    !extension.equals(".jpeg") &&
                    !extension.equals(".gif")  &&
                    !extension.equals(".png") ){

                model.addAttribute("result", 2);
                return "teacher/update";
            }
            UUID uuid =UUID.randomUUID();
            newfilename = uuid.toString() + extension;
            mf.transferTo(new File(path + "/"+ newfilename));

            dbTeacher.setTeaImage(newfilename);
        }
        if(passwordEncoder.matches(teacherDto.getTeaPwd(), dbTeacher.getTeaPwd())) {
            if (size <= 0) {
                teacherDto.setTeaImage(dbTeacher.getTeaImage()); // Preserve old image if new one is not uploaded
            } else {
                teacherDto.setTeaImage(newfilename); // Update with new image
            }
            // 비밀번호 일치: 회원 정보 업데이트
            teacherService.update(teacherDto);
            System.out.println("정보 수정 완료");
            model.addAttribute("result", 1);
            System.out.println("teacherDto.teaImage"+teacherDto.getTeaImage());

            return "redirect:/teacher/myPage"; // 정보 업데이트 후 마이 페이지로 리다이렉트
        } else{ //비밀번호 불일치
            model.addAttribute("result", -1);
            return "teacher/updateForm";
        }
    }


    //강의 등록
    @GetMapping("/lecJoin")
    public String lecJoin(HttpSession session, Model model){

        Integer teaNo = (Integer) session.getAttribute("teaNo");
        model.addAttribute("teaNo",teaNo);

        return "teacher/lecJoinForm";
    }
    //강의 등록
    @PostMapping("/lecJoin")
    public String lecJoin(@ModelAttribute LectureDto lectureDto,
                          HttpSession session, Model model) {
        int result = 0;
        lectureService.lecJoin(lectureDto);
        System.out.println("lectureDto: "+ lectureDto);

        model.addAttribute("result",result);

        return "teacher/lecJoin";

    }
    //강사 강의 목록 조희
    @GetMapping("/leclist")
    public String leclist(HttpSession session,
                          Model model){
        Integer teaNo = (Integer) session.getAttribute("teaNo");
        LectureDto lectureDto = new LectureDto();
        lectureDto.setTea_no(teaNo);

        List<LectureDto> lecture = lectureService.findAllByTeaNo(lectureDto.getTea_no());
        model.addAttribute("lecture",lecture);
        return "teacher/leclist";
    }

}