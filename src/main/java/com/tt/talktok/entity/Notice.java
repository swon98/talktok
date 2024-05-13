package com.tt.talktok.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no_no")
    private int noNo;
    @Column(name = "no_subject")
    private String noSubject;
    @Column(name = "no_content")
    private String noContent;
    @Column(name = "no_readcount")
    private int noReadcount;
    @Column(name = "no_date")
    private String noDate;
}
