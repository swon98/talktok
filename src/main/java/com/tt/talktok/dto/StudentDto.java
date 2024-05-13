package com.tt.talktok.dto;

import lombok.*;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
public class StudentDto {

    private int stuNo;
    private String stuName;
    private String stuEmail;
    private String stuPwd;
    private String stuPhone;
    private String stuNickname;
    private String stuSocial = "normal"; // 기본값 설정
    public StudentDto() {
        // 기본 생성자 추가
        this.stuSocial = "normal"; // 기본값으로 "normal" 설정
    }

}
