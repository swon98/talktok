package com.tt.talktok.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ca_no")
    private int caNo;

    @ManyToOne
    @JoinColumn(name = "stu_no", referencedColumnName = "stuNo")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "lec_no", referencedColumnName = "lec_no")
    private Lecture lecture;

    @Column(name = "ca_count")
    private int caCount;

    @Column(name = "lec_price")
    private String lecPrice;

}
