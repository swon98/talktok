package com.tt.talktok.dto;

import com.tt.talktok.entity.Teacher;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class TeacherDto {
    private int teaNo;
    private String teaName;
    private String teaEmail;
    private String teaPwd;
    private String teaPhone;
    private String teaNickname;
    private String teaAccount;
    private String teaIntro;
    private String teaDetail;
    private String teaCareer;
    private String teaImage;

    public TeacherDto() {
        // 기본 생성자 추가
    }


}
