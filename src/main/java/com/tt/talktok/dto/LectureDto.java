package com.tt.talktok.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LectureDto {
    private int lec_no;
    private String lec_name;
    private String lec_day;
    private String lec_time;
    private String lec_price;
    private String lec_detail;
    private String lec_startdate;
    private String lec_enddate;
    private int tea_no;
}
