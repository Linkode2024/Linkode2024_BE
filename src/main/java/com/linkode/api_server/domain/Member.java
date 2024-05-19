package com.linkode.api_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String githubId;

    private String nickName;

    private String status;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<MemberStudyroom> memberStudyroomList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Data> dataList= new ArrayList<>();


    /** 캐릭터와의 연관관계의 주인 */
    @OneToOne
    @JoinColumn(name = "character_id")
    private Character character;

}
