package com.linkode.api_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Character {


    @Id
    @GeneratedValue
    @Column(name = "character_id")
    private Long id;

    private String name;

    private String description;

    @OneToOne(mappedBy = "character")
    private Member member;

}
