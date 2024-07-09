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
public class Color extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long colorId;

    @Column(nullable = false)
    private String hexCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToMany(mappedBy = "avatar")
    private List<Member> members = new ArrayList<>();
}
