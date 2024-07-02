package com.linkode.api_server.domain;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Avatar extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id", nullable = false)
    private Long avatarId;

    @Column(nullable = false)
    private String avatarImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToMany(mappedBy = "avatar")
    private List<Member> members = new ArrayList<>();

    public Avatar(Long avatarId, String avatarImg, BaseStatus status) {
        this.avatarId = avatarId;
        this.avatarImg = avatarImg;
        this.status = status;
    }

}
