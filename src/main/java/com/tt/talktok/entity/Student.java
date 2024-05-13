package com.tt.talktok.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "student")
public class Student {

    // stu_no를 기본키로 설정합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가(auto-increment)
    private int stuNo;
    private String stuName;
    private String stuEmail;
    private String stuPwd;
    private String stuPhone;
    private String stuNickname;
    private String stuSocial;

/*    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Review> reviews;*/

    public Student() {
        // 기본 생성자 추가
    }

    //수강 장바구니 조인
    @OneToMany(mappedBy = "student")
    private List<Cart> carts = new ArrayList<>();

}
