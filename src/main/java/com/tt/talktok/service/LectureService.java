package com.tt.talktok.service;

import com.tt.talktok.dto.LectureDto;
import com.tt.talktok.entity.Lecture;
import com.tt.talktok.repository.LectureRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class LectureService {

    private final LectureRepository lectureRepository;


    // 강의 리스트 가져오기
    /*public List<LectureDto> findAll() {
        List<Lecture> lectureList = lectureRepository.findAll();
        return lectureList.stream() // 각각의 Lecture 엔티티를 LectureDto 객체로 변환하는 스트림
                .map(this::convertToDto) // 이때 map() 메서드에 전달되는 convertToDto 메서드가 사용된다
                // convertToDto 는 강의 엔티티를 DTO 로 변환하는 메서드. 각각의 필드에 대해 엔티티의 값을 DTO 에 복사
                .collect(Collectors.toList()); // 스트림에서 변환된 DTO 객체들을 리스트로 수집한다
    }*/
    public Page<LectureDto> findAll(Pageable pageable) {
        Page<Lecture> lecturePage = lectureRepository.findAll(pageable);
        return lecturePage.map(this::convertToDto);
    }

    // convertToDto
    private LectureDto convertToDto(Lecture lecture) { // Lecture 엔티티를 파라미터로 받는다
        LectureDto dto = new LectureDto(); // 새로운 LectureDto 객체를 생성
        dto.setLec_no(lecture.getLecNo()); // 엔티티의 각 필드 값을 DTO 의 해당 필드에 복사한다
        dto.setLec_name(lecture.getLec_name());
        dto.setLec_day(lecture.getLec_day());
        dto.setLec_time(lecture.getLec_time());
        dto.setLec_price(lecture.getLec_price());
        dto.setLec_detail(lecture.getLec_detail());
        dto.setLec_startdate(lecture.getLecStartdate());
        dto.setLec_enddate(lecture.getLec_enddate());
        dto.setTea_no(lecture.getTeaNo());
        return dto; // 마지막으로 변환된 LectureDto 객체를 반환한다
    }

    private Lecture convertToEntity(LectureDto dto) { // LectureDto 객체를 파라미터로 받는다
        Lecture lecture = new Lecture(); // 새로운 Lecture 엔티티 객체를 생성
        lecture.setLecNo(dto.getLec_no()); // DTO의 각 필드 값을 엔티티의 해당 필드에 복사한다
        lecture.setLec_name(dto.getLec_name());
        lecture.setLec_day(dto.getLec_day());
        lecture.setLec_time(dto.getLec_time());
        lecture.setLec_price(dto.getLec_price());
        lecture.setLec_detail(dto.getLec_detail());
        lecture.setLecStartdate(dto.getLec_startdate());
        lecture.setLec_enddate(dto.getLec_enddate());
        lecture.setTeaNo(dto.getTea_no());
        return lecture; // 마지막으로 변환된 Lecture 엔티티 객체를 반환한다
    }

    // 강의 하나 가져오기
    public LectureDto findLectureByLecNo(int lec_no) {
        Lecture lecture = lectureRepository.findByLecNo(lec_no);
        if (lecture != null) {
            return convertToDto(lecture);
        } else { // 강의가 없을 경우 처리
            return null;
        }
    }

    //강의 생성
    public void lecJoin(LectureDto lectureDto) {
        Lecture newlecture = convertToEntity(lectureDto);
        lectureRepository.save(newlecture);
    }
    //강사 자기 강의 조회
    public List<LectureDto> findAllByTeaNo(int tea_no) {
        List<Lecture> lectureList = lectureRepository.findAllByTeaNoOrderByLecStartdateDesc(tea_no);
        return lectureList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
//    public List<LectureDto> findAllByTeaNo(int tea_no) {
//        List<Lecture> lectureList = lectureRepository.findAllByTeaNoOrderByLecStartdateDesc(tea_no);
//        return lectureList.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }


}
