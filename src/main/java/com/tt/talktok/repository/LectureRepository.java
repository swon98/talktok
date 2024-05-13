package com.tt.talktok.repository;

import com.tt.talktok.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    // 강의 리스트
    Page<Lecture> findAll(Pageable pageable);

    //강의 하나
    Lecture findByLecNo(int lec_no);

    //강사 자기 강의 조회
    List<Lecture> findAllByTeaNoOrderByLecStartdateDesc(int teaNo);

}
