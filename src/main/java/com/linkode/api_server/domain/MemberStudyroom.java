package com.linkode.api_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class MemberStudyroom {

    @Id
    @GeneratedValue
    @Column(name = "member_studyroom_id")
    private Long id;

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
