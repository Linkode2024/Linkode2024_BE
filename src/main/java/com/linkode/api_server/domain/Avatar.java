package com.linkode.api_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Avatar {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    @Column(name = "avatar_id")
    private Long id;

    private String name;

    private String description;

    @OneToOne(mappedBy = "avatar")
    private Member member;

}
