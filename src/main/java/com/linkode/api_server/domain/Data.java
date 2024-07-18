package com.linkode.api_server.domain;


import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Data extends BaseTime {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id", nullable = false)
    private Long dataId;

    @Column(nullable = false)
    private String dataName;

    @Column(nullable = false)
    private String dataType;

    @Column(nullable = false)
    private String dataUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;


    /** 맴버와의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /** 스터디룸과의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "studyroom_id")
    private Studyroom studyroom;


    public Data(String dataName, String dataType, String dataUrl, BaseStatus status, Member member, Studyroom studyroom) {
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataUrl = dataUrl;
        this.status = status;
        this.member = member;
        this.studyroom = studyroom;
    }
}
