package com.tt.talktok.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class PaymentDto {
    private int pay_no;
    private Timestamp pay_time;
    private String pay_price;
    private int lec_no;
    private String lec_name;
    private String stu_Email;
}
