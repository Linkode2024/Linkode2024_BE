package com.linkode.api_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Studyroom {


    @Id
    @GeneratedValue
    @Column(name = "studyroom_id")
    private Long id;

    private String studyRoomName;

    private String studyRoomProfile;

    private String status;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "studyroom")
    private List<MemberStudyroom> memberStudyroomList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "studyroom")
    private List<Data> dataList= new ArrayList<>();


}
