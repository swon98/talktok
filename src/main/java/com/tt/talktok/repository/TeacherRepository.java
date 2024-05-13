package com.tt.talktok.repository;

import com.tt.talktok.entity.Student;
import com.tt.talktok.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Page<Teacher> findByTeaNameContaining(String keyword,Pageable pageable);

    Teacher findTeacherByTeaEmail(String teaEmail);

    void deleteTeacherByTeaEmail(String teaEmail);

    Teacher findTeacherByTeaNo(int teaNo);
}
