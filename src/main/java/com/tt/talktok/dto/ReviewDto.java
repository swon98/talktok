package com.tt.talktok.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class ReviewDto {

    private int rev_no;
    private String rev_name;
    private String rev_detail;
    private String rev_writer;
    private Integer rev_ReadCount;
    private int rev_score;
    private Timestamp rev_date;
    private int lec_no;
    private String lec_name;
    private int tea_no;
    private String tea_name;
    private int stu_no;
}
