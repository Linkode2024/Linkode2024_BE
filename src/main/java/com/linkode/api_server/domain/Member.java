package com.linkode.api_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkode.api_server.domain.Avatar;
import com.linkode.api_server.domain.Data;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false)
    private String githubId;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private BaseStatus status;


    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<MemberStudyroom> memberStudyroomList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Data> dataList= new ArrayList<>();


    /** 캐릭터와의 연관관계의 주인 */
    @OneToOne
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

}
