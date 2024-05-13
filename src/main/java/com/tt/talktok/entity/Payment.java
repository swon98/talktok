package com.tt.talktok.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_no")
    private int payNo;
    @Column(name = "pay_time")
    private Timestamp payTime;
    @Column(name = "payPrice")
    private String pay_price;
    @Column(name = "lec_no")
    private int lecNo;
    @Column(name = "lecName")
    private String lec_name;
    @Column(name = "stu_email")
    private String stuEmail;
}
