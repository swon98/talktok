package com.tt.talktok.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lecture")
public class Lecture {
    @Id
    @Column(name = "lec_no")
    private int lecNo;
    private String lec_name;
    private String lec_day;
    private String lec_time;
    private String lec_price;
    private String lec_detail;
    @Column(name="lec_startdate")
    private String lecStartdate;

    private String lec_enddate;
    @Column(name = "tea_no")
    private int teaNo;
}
