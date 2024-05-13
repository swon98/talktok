package com.tt.talktok.dto;

import com.tt.talktok.entity.Lecture;
import com.tt.talktok.entity.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDto {

    private Long dibNo;
    private Student student;
    private Lecture lecture;
    private int caCount;
    private String lecPrice;

}
