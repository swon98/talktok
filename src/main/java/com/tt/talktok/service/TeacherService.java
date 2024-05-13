package com.tt.talktok.service;

import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.dto.TeacherDto;
import com.tt.talktok.entity.Student;
import com.tt.talktok.entity.Teacher;
import com.tt.talktok.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Builder
@Service
@RequiredArgsConstructor
public class TeacherService{
    private final TeacherRepository teacherRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // DTO에서 엔터티로 변환하는 메서드
    public Teacher convertToEntity(TeacherDto dto) {
        return Teacher.builder()
                .teaNo(dto.getTeaNo())
                .teaName(dto.getTeaName())
                .teaEmail(dto.getTeaEmail())
                .tea_pwd(dto.getTeaPwd())
                .tea_phone(dto.getTeaPhone())
                .tea_nickname(dto.getTeaNickname())
                .tea_account(dto.getTeaAccount())
                .tea_intro(dto.getTeaIntro())
                .tea_detail(dto.getTeaDetail())
                .tea_career(dto.getTeaCareer())
                .tea_image(dto.getTeaImage())
                .build();
    }
    // 엔터티에서 DTO로 변환하는 메서드
    public TeacherDto convertToDto(Teacher entity) {
        return TeacherDto.builder()
                .teaNo(entity.getTeaNo())
                .teaName(entity.getTeaName())
                .teaEmail(entity.getTeaEmail())
                .teaPwd(entity.getTea_pwd())
                .teaPhone(entity.getTea_phone())
                .teaNickname(entity.getTea_nickname())
                .teaAccount(entity.getTea_account())
                .teaIntro(entity.getTea_intro())
                .teaDetail(entity.getTea_detail())
                .teaCareer(entity.getTea_career())
                .teaImage(entity.getTea_image())
                .build();
    }

    //선생 목록 조회
    public Page<TeacherDto> teacherList(Pageable pageable){
        Page<Teacher> teachers = teacherRepository.findAll(pageable);
        return teachers.map(this::convertToDto);
    }
    //선생 검색 기능
    public Page<TeacherDto> teacherSearchList(String keyword, Pageable pageable){
        Page<Teacher> teacherlists = teacherRepository.findByTeaNameContaining(keyword,pageable);
        return teacherlists.map(this::convertToDto);
    }

    //선생 상세페이지 조회
    public TeacherDto getTeacherDetail(int tea_no){
        Teacher teacherdetails = teacherRepository.findById(tea_no).orElse(null);
        return convertToDto(teacherdetails);
    }
    // 강사 정보 조회
    public TeacherDto findTeacher(String teaEmail) {
        System.out.println("서비스 이동");
        Teacher dbTeacher = teacherRepository.findTeacherByTeaEmail(teaEmail);
        TeacherDto dbTeacherDto = new TeacherDto();
        if(dbTeacher !=null) {
            System.out.println("dbTeacher not null");
            dbTeacherDto=convertToDto(dbTeacher);
        }
        return dbTeacherDto;
    }
    // 강사 정보 조회
    public TeacherDto findTeacher(int tea_no) {
        Teacher dbTeacher = teacherRepository.findTeacherByTeaNo(tea_no);
        TeacherDto dbTeacherDto = new TeacherDto();
        if(dbTeacher !=null) {
            dbTeacherDto=convertToDto(dbTeacher);
        }
        return dbTeacherDto;
    }

    // 비밀번호 업데이트/변경
    public void updatePwd(TeacherDto teacherDto) {
        String teaEmail = teacherDto.getTeaEmail();
        Teacher newTeacher = teacherRepository.findTeacherByTeaEmail(teaEmail);

        newTeacher.setTea_pwd(teacherDto.getTeaPwd());

        teacherRepository.save(newTeacher);


    }

    // 회원정보 수정
    @Transactional
    public void update(TeacherDto teacherDto) {
        Teacher teacher = teacherRepository.findTeacherByTeaEmail(teacherDto.getTeaEmail());
        if (teacher != null) {
            // DTO에서 변경된 정보를 Entity에 반영
            teacher.setTeaName(teacherDto.getTeaName());
            teacher.setTea_phone(teacherDto.getTeaPhone());
            teacher.setTea_nickname(teacherDto.getTeaNickname());
            teacher.setTeaEmail(teacherDto.getTeaEmail());
            teacher.setTea_phone(teacherDto.getTeaPhone());
            teacher.setTea_account(teacherDto.getTeaAccount());
            teacher.setTea_intro(teacherDto.getTeaIntro());
            teacher.setTea_detail(teacherDto.getTeaDetail());
            teacher.setTea_career(teacherDto.getTeaCareer());
            teacher.setTea_image(teacherDto.getTeaImage());
            teacher.setTea_pwd(passwordEncoder.encode(teacherDto.getTeaPwd())); // 비밀번호도 업데이트할 경우
            teacherRepository.save(teacher);
        }
    }

    // 강사 회원 가입
    public void join(TeacherDto teacherDto) {
        System.out.println("회원가입 처리: " + teacherDto);
        Teacher newTeacher = new Teacher();

        String pwd=teacherDto.getTeaPwd();
        String encodePwd = passwordEncoder.encode(pwd);

        newTeacher = convertToEntity(teacherDto);
        newTeacher.setTea_pwd(encodePwd);
        teacherRepository.save(newTeacher);

    }

    @Transactional
    public void withdraw(String teaEmail) {
        teacherRepository.deleteTeacherByTeaEmail(teaEmail);
    }



}