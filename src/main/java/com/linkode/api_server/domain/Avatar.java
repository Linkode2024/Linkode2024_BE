package com.linkode.api_server.domain;

import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Avatar extends BaseTime {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id", nullable = false)
    private Long avatarId;

    @Column(nullable = false)
    private String avatarImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private BaseStatus status;

    @OneToOne(mappedBy = "avatar")
    private Member member;

}
