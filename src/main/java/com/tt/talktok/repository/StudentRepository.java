package com.tt.talktok.repository;

import com.tt.talktok.dto.StudentDto;
import com.tt.talktok.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findStudentByStuEmail(String stuEmail);

    void deleteStudentByStuEmail(String stuEmail);

}
