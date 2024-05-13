package com.tt.talktok.service;

import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.dto.TeacherDto;
import com.tt.talktok.entity.Student;
import com.tt.talktok.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Builder
@Service
@RequiredArgsConstructor
public class StudentService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;

    //entity의 내용을 dto로 이동
    public StudentDto convertToDto(Student entity){
        return StudentDto.builder()
                .stuNo(entity.getStuNo())
                .stuName(entity.getStuName())
                .stuEmail(entity.getStuEmail())
                .stuPwd(entity.getStuPwd())
                .stuPhone(entity.getStuPhone())
                .stuNickname(entity.getStuNickname())
                .stuSocial(entity.getStuSocial())
                .build();
    }

    //entity의 내용을 dto로 이동
    public Student convertToEntity(StudentDto dto){
        return Student.builder()
                .stuNo(dto.getStuNo())
                .stuName(dto.getStuName())
                .stuEmail(dto.getStuEmail())
                .stuPwd(dto.getStuPwd())
                .stuPhone(dto.getStuPhone())
                .stuNickname(dto.getStuNickname())
                .stuSocial(dto.getStuSocial())
                .build();
    }
    // 학생 정보 조회
    public StudentDto findStudent(String stuEmail) {

        Student dbStudent = studentRepository.findStudentByStuEmail(stuEmail);
        StudentDto dbStudentDto = new StudentDto();
        if(dbStudent !=null) {
            dbStudentDto=convertToDto(dbStudent);
        }
        return dbStudentDto;
    }

    // 학생 회원 가입
    public void join(StudentDto studentDto) {
        System.out.println("서비스 진입");
        Student newStudent = new Student();

        String pwd=studentDto.getStuPwd();
        String encodePwd = passwordEncoder.encode(pwd);

        newStudent = convertToEntity(studentDto);
        newStudent.setStuPwd(encodePwd);

        studentRepository.save(newStudent);
    }
    @Transactional
    public void withdraw(String stuEmail) {
        studentRepository.deleteStudentByStuEmail(stuEmail);
    }

    // 비밀번호 업데이트/변경
    public void updatePwd(StudentDto studentDto) {
        String stuEmail = studentDto.getStuEmail();
        Student newStudent = studentRepository.findStudentByStuEmail(stuEmail);

        newStudent.setStuPwd(studentDto.getStuPwd());

        studentRepository.save(newStudent);


    }
    // 회원정보 수정
    public void update(StudentDto studentDto) {
        Student student = studentRepository.findStudentByStuEmail(studentDto.getStuEmail());
        if (student != null) {
            // DTO에서 변경된 정보를 Entity에 반영
            student.setStuName(studentDto.getStuName());
            student.setStuPhone(studentDto.getStuPhone());
            student.setStuNickname(studentDto.getStuNickname());
            studentRepository.save(student);
        }
    }


}



