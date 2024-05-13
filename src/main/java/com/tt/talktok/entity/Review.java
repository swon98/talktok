package com.tt.talktok.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rev_no")
    private int revNo;

    @Column(name = "rev_name")
    private String revName;
    @Column(name = "rev_detail")
    private String revDetail;
    @Column(name = "rev_writer")
    private String revWriter;
    @Column(name = "rev_readcount")
    private int  revReadCount;
    @Column(name = "rev_score")
    private int revScore;
    @CreationTimestamp
    @Column(name = "rev_date")
    private Timestamp revDate;
    @Column(name = "lec_no")
    private int lecNo;
    @Column(name = "lec_name")
    private String lecName;
    @Column(name = "tea_name")
    private String teaName;
    @Column(name = "tea_no")
    private int teaNo;
    @Column(name = "stu_no")
    private int stuNo;

/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
*/
}
