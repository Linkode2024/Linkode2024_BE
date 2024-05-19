package com.linkode.api_server.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    @Column(name = "data_id")
    private Long id;

    private String dataName;

    private String dataType;

    private String dataUrl;

    private String status;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    /** 맴버와의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /** 스터디룸과의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "studyroom_id")
    private Studyroom studyroom;
}
