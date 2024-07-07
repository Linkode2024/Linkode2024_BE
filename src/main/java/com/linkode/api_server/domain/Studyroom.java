package com.linkode.api_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.domain.base.BaseTime;
import com.linkode.api_server.domain.memberstudyroom.MemberStudyroom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Studyroom extends BaseTime {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyroom_id", nullable = false)
    private Long studyroomId;

    @Column(nullable = false)
    private String studyroomName;

    @Column(nullable = false)
    private String studyroomProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "studyroom")
    private List<MemberStudyroom> memberStudyroomList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "studyroom")
    private List<Data> dataList= new ArrayList<>();


    public Studyroom(String studyroomName, String studyroomProfile, BaseStatus status) {
        this.studyroomName = studyroomName;
        this.studyroomProfile = studyroomProfile;
        this.status = status;
    }

    public void updateStudyroomInfo(String studyroomName, String studyroomProfile){
        this.studyroomName = studyroomName;
        this.studyroomProfile = studyroomProfile;
    }
}
