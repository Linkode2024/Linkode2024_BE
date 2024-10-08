package com.linkode.api_server.domain.data;


import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Column(nullable = false,length = 2048)
    private String dataName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private DataType dataType;

    @Column(nullable = false,length = 2048)
    private String dataUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    /** OpenGraph 메타데이터 필드들 */
    @Column(nullable = true)
    private String ogTitle;

    @Column(nullable = true)
    private String ogDescription;

    @Column(nullable = true)
    private String ogImage;

    @Column(nullable = true)
    private String ogUrl;

    @Column(nullable = true)
    private String ogType;

    /** 맴버와의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /** 스터디룸과의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "studyroom_id")
    private Studyroom studyroom;

    @Builder
    public Data(String dataName, DataType dataType, String dataUrl, String ogTitle, String ogDescription,
                String ogImage, String ogUrl, String ogType, BaseStatus status, Member member, Studyroom studyroom) {
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataUrl = dataUrl;
        this.ogTitle = ogTitle;
        this.ogDescription = ogDescription;
        this.ogImage = ogImage;
        this.ogUrl = ogUrl;
        this.ogType = ogType;
        this.status = status;
        this.member = member;
        this.studyroom = studyroom;
    }

    public void updateDataStatus(BaseStatus status){
        this.status = status;
    }
}
