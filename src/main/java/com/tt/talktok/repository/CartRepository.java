package com.tt.talktok.repository;

import com.tt.talktok.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findByStudentStuNo(int stuNo);

    int countByLectureLecNoAndStudentStuNo(int lecNo, int stuNo);

    void deleteByStudentStuNoAndLectureLecNo(int stuNo, int lecNo);

}

