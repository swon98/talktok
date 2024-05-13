package com.tt.talktok.repository;

import com.tt.talktok.dto.ReviewDto;
import com.tt.talktok.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findAll(Pageable pageable);

    Review findByRevNo(int rev_no);

    List<Review> findByTeaNo(int tea_no);

    Page<Review> findByStuNo(int stu_no, Pageable pageable);

    Page<Review> findByLecNameContaining(Pageable pageable, String keyword);

    Page<Review> findByTeaNameContaining(Pageable pageable, String keyword);

    Page<Review> findByRevNameContaining(Pageable pageable, String keyword);

    Page<Review> findByRevDetailContaining(Pageable pageable, String keyword);

    //Page<Review> findByRevNameContainingAndRevDetailContaining(Pageable pageable, String keyword);
    // 특정 학생이 작성한 후기 리스트를 학생 번호(stu_no)로 검색
    List<Review> findByStuNo(int stuNo);

    @Transactional
    @Modifying
    @Query(value = "update Review set revReadCount=revReadCount+1 where revNo=:revNo")
    void reviewCountUpdate(int revNo);

//    @Query("SELECT COUNT(*) FROM Review r WHERE r.stuNo = :stuNo AND r.lecNo = :lecNo")
//    List<Integer> existsByStuNoAndLecNo(List<Integer> reviewCheck, int stuNo);
    @Query(value = "SELECT COUNT(*) FROM Review r WHERE r.stuNo =:stuNo AND r.lecNo =:lecNo")
    int existsByStuNoAndLecNo(@Param("stuNo")int stuNo,@Param("lecNo")int lecNo);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.revName = :#{#review.revName}, r.revDetail = :#{#review.revDetail}, r.revScore = :#{#review.revScore} WHERE r.revNo = :#{#review.revNo}")
    void updateReviewDetails(Review review);


//    @Transactional
//    @Modifying
//    @Query(value = "update Review set revName=:revName, revDetail=:revDetail, revScore=:revScore where revNo=:revNo")
//    void reviewUpdate(ReviewDto reviewDto);

    // 메인에 베스트 강의 번호 가져가기
    @Query(value = "SELECT lec_no " +
            "FROM review " +
            "GROUP BY lec_no " +
            "ORDER BY COUNT(lec_no) DESC " +
            "LIMIT 0, 5", nativeQuery = true)
    List<Integer> findBestLecNo();
    
    // 메인에 베스트 선생님 번호 가져가기
    @Query(value = "SELECT tea_no " +
            "FROM review " +
            "GROUP BY tea_no " +
            "ORDER BY COUNT(tea_no) DESC " +
            "LIMIT 0, 4", nativeQuery = true)
    List<Integer> findBestTeaNo();
}

