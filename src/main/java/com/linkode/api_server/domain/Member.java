package com.linkode.api_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkode.api_server.domain.data.Data;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(name="idx_github_id_status", columnList = "github_id, status")})
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
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<MemberStudyroom> memberStudyroomList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Data> dataList= new ArrayList<>();

    /** 캐릭터와의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    /** 캐릭터와의 연관관계의 주인 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    public Member(Long memberId, String githubId, String nickname, Color color,BaseStatus status, Avatar avatar) {
        this.memberId = memberId;
        this.githubId = githubId;
        this.nickname = nickname;
        this.color=color;
        this.status = status;
        this.avatar = avatar;
    }

    public Member(String githubId, String nickname, Avatar avatar, Color color, BaseStatus status) {
        this.githubId = githubId;
        this.nickname = nickname;
        this.color = color;
        this.avatar = avatar;
        this.status = status;
    }

    public void updateMemberStatus(BaseStatus status){
        this.status = status;
    }
  
    public void updateMemberInfo(String nickname, Avatar avatar, Color color){
        this.nickname=nickname;
        this.avatar=avatar;
        this.color=color;
    }

}
